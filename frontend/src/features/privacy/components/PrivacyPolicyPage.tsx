import { Link } from 'react-router';
import { Alert } from '@/shared/components/Alert';
import { usePageTitle } from '@/shared/hooks/usePageTitle';
import {
  DATA_CONTROLLER_NAME,
  PRIVACY_POLICY_EFFECTIVE_DATE,
  PRIVACY_POLICY_VERSION,
  privacyContactEmail,
} from '../model/privacyPolicy';

/** Public data processing policy (HU-05, RF-03), written in plain Spanish. */
export function PrivacyPolicyPage() {
  usePageTitle('Política de datos');
  const contactEmail = privacyContactEmail();

  return (
    <article>
      <h1>Política de tratamiento de datos</h1>
      <p className="muted">
        Versión {PRIVACY_POLICY_VERSION} · Vigente desde el {PRIVACY_POLICY_EFFECTIVE_DATE}
      </p>
      <Alert>Borrador: este texto todavía no tiene una revisión legal.</Alert>

      <h2>¿Quién es el responsable?</h2>
      <p>
        {DATA_CONTROLLER_NAME} es el responsable del tratamiento de tus datos personales en
        Podcuaderno.
      </p>

      <h2>¿Qué datos guardamos?</h2>
      <ul>
        <li>Tu correo electrónico.</li>
        <li>Tu contraseña, cifrada de forma que nadie la puede leer.</li>
        <li>La fecha en que aceptas esta política y la versión que aceptaste.</li>
        <li>Tus cuadernos, con sus temas y conceptos.</li>
        <li>
          Eventos de uso: solo el tipo de acción y la cantidad de caracteres, nunca el contenido de
          tus apuntes.
        </li>
      </ul>

      <h2>¿Para qué los usamos?</h2>
      <p>
        Para crear y proteger tu cuenta, enviarte los correos de la cuenta (por ejemplo, el enlace
        para activarla) y guardar tus cuadernos para que los uses desde cualquier dispositivo. Los
        eventos de uso nos sirven para saber, en general, cuánto se usa Podcuaderno.
      </p>
      <p>
        Para enviar esos correos usamos Resend, un proveedor de envío de correos ubicado en Estados
        Unidos. Resend recibe tu correo electrónico como encargado del tratamiento y solo lo usa
        para enviar los correos de tu cuenta. Esto implica una transferencia internacional de tus
        datos, según la Ley 1581 de 2012.
      </p>

      <h2>Lo que nunca hacemos</h2>
      <p>
        No vendemos tus datos y no compartimos tus apuntes con nadie. La voz gratuita se genera en
        tu navegador, así que tus apuntes no salen de tu dispositivo para convertirse en audio.
      </p>

      <h2>¿Cuánto tiempo los guardamos?</h2>
      <p>Guardamos tus datos mientras exista tu cuenta.</p>

      <h2>Tus derechos (Ley 1581 de 2012)</h2>
      <p>Según la Ley 1581 de 2012 y el Decreto 1377 de 2013, puedes:</p>
      <ul>
        <li>Conocer los datos que tenemos sobre ti.</li>
        <li>Actualizarlos y rectificarlos.</li>
        <li>Pedir que los suprimamos.</li>
        <li>Revocar la autorización que nos diste para tratarlos.</li>
      </ul>

      <h2>¿Cómo ejercerlos?</h2>
      {contactEmail ? (
        <p>
          Escríbenos a <a href={`mailto:${contactEmail}`}>{contactEmail}</a>.
        </p>
      ) : (
        <p>
          <strong>Pronto publicaremos el correo para ejercer estos derechos.</strong>
        </p>
      )}
      <p>
        Respondemos las consultas en máximo 10 días hábiles y los reclamos en máximo 15 días
        hábiles.
      </p>

      <h2>Cambios a esta política</h2>
      <p>
        Si cambiamos esta política, publicaremos una versión nueva y te pediremos que la aceptes
        otra vez.
      </p>

      <p>
        <Link to="/">Volver al inicio</Link>
      </p>
    </article>
  );
}
