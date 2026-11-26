import { expect, test } from '@playwright/test';

test('home page shows the product name', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByRole('heading', { name: 'Podcuaderno' })).toBeVisible();
  await expect(page).toHaveTitle(/Podcuaderno/);
});
