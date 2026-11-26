import { expect, test, type APIRequestContext } from '@playwright/test';

const MAILPIT_URL = process.env.MAILPIT_URL;

test.skip(!MAILPIT_URL, 'Needs the docker compose stack (E2E_BASE_URL and MAILPIT_URL).');

interface MailpitSearch {
  readonly messages: readonly { readonly ID: string }[];
}

/** Reads the verification link from the email caught by Mailpit. */
async function verificationLink(request: APIRequestContext, email: string): Promise<string> {
  let messageId = '';
  await expect
    .poll(
      async () => {
        const response = await request.get(`${MAILPIT_URL}/api/v1/search`, {
          params: { query: `to:"${email}"` },
        });
        const body = (await response.json()) as MailpitSearch;
        messageId = body.messages[0]?.ID ?? '';
        return messageId;
      },
      { timeout: 20_000 },
    )
    .not.toBe('');
  const response = await request.get(`${MAILPIT_URL}/api/v1/message/${messageId}`);
  const message = (await response.json()) as { Text: string };
  const link = message.Text.split(/\s+/).find((word) => word.includes('/verificar-correo#token='));
  expect(link, 'verification link in the email').toBeTruthy();
  return link ?? '';
}

test('a new student registers, verifies the email, logs in and creates a notebook', async ({
  page,
  request,
}) => {
  const email = `e2e-${crypto.randomUUID()}@example.test`;
  const password = 'clave-de-prueba-123';

  await page.goto('/registro');
  await page.getByLabel('Correo').fill(email);
  await page.getByLabel('Contraseña', { exact: true }).fill(password);
  const createAccount = page.getByRole('button', { name: 'Crear cuenta' });
  await expect(createAccount).toBeDisabled();
  await page.getByLabel(/Leí y acepto la política de datos/).check();
  await createAccount.click();
  await expect(page.getByRole('heading', { name: 'Revisa tu correo' })).toBeVisible();

  await page.goto(await verificationLink(request, email));
  await expect(page.getByRole('heading', { name: 'Tu cuenta está activa' })).toBeVisible();
  await expect(page).not.toHaveURL(/token=/);
  await page.getByRole('main').getByRole('link', { name: 'Iniciar sesión' }).click();

  await page.getByLabel('Correo').fill(email);
  await page.getByLabel('Contraseña', { exact: true }).fill(password);
  await page.getByRole('button', { name: 'Entrar' }).click();
  await expect(page.getByRole('heading', { name: 'Mis cuadernos' })).toBeVisible();
  await expect(page.getByText('Todavía no tienes cuadernos.')).toBeVisible();

  await page.getByRole('button', { name: 'Nuevo cuaderno' }).click();
  const dialog = page.getByRole('dialog', { name: 'Nuevo cuaderno' });
  await dialog.getByLabel('Nombre del cuaderno').fill('Bases de datos');
  await dialog.getByRole('button', { name: 'Crear' }).click();
  await expect(page.getByRole('listitem').filter({ hasText: 'Bases de datos' })).toBeVisible();

  await page.reload(); // the session survives a reload thanks to the refresh cookie
  await expect(page.getByRole('listitem').filter({ hasText: 'Bases de datos' })).toBeVisible();
});
