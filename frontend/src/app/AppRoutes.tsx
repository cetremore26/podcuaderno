import { Route, Routes } from 'react-router';
import { CheckEmailPage, RegisterPage, VerifyEmailPage } from '@/features/auth';
import { PrivacyPolicyPage } from '@/features/privacy';
import { AppLayout } from './AppLayout';
import { HomePage } from './HomePage';
import { NotFoundPage } from './NotFoundPage';

/** Routes of the interface, in Spanish because they are visible to the user (ADR-0011). */
export function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<HomePage />} />
        <Route path="registro" element={<RegisterPage />} />
        <Route path="registro/revisa-tu-correo" element={<CheckEmailPage />} />
        <Route path="verificar-correo" element={<VerifyEmailPage />} />
        <Route path="politica-de-datos" element={<PrivacyPolicyPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
}
