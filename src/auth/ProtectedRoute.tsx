import React from 'react'
import { Navigate, Outlet } from 'react-router-dom'

// 1. Esta es la función que comprueba la autenticación.
// Por ahora, solo simula comprobando si existe un 'authToken' en localStorage.
const useAuth = () => {
  // En una app real, aquí deberías validar el token (si no ha expirado, etc.)
  const token = localStorage.getItem('accessToken');
  return token && token !== "" ? true : false;
};

const ProtectedRoute: React.FC = () => {
  const isAuth = useAuth();

  // 2. Si isAuth es true, <Outlet /> renderiza el componente hijo (Dashboard).
  //    Si es false, <Navigate /> redirige al usuario a /login.
  return isAuth ? <Outlet /> : <Navigate to="/login" replace />;
}

export default ProtectedRoute