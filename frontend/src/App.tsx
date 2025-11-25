import { Route, Routes, BrowserRouter as Router } from "react-router-dom"
import "./index.css"
import MainLayout from "./layouts/MainLayout"
import Home from "./pages/Home"
import Register from "./pages/Register"
import Login from "./pages/Login"
import Nosotros from "./pages/Nosotros"
import ForgotPassword from "./pages/ForgotPassword"
import ResetPassword from "./pages/ResetPassword"
import Onboarding from "./pages/Onboarding"
import NotFound from "./pages/NotFound"

// --- NUEVOS IMPORTS ---
// Rutas Protegidas
import Dashboard from "./pages/Dashboard"
import ProtectedRoute from "./auth/ProtectedRoute"
import DashboardLayout from "./layouts/DashboardLayout"
import Perfil from "./pages/Perfil"
import Entrenamiento from "./pages/Entrenamiento"
import Seguimiento from "./pages/Seguimiento"
import Nutricion from "./pages/Nutricion"
import Comunidad from "./pages/Comunidad"
import Sesiones from "./pages/Sesiones"
import RetoSemanal from "./pages/RetoSemanal"
import Articulos from "./pages/Articulos"
import Ejercicios from "./pages/Ejercicios"
import CrearEntrenamiento from "./pages/CrearEntrenamiento"
import CrearRutina from "./pages/CrearRutina"
import CrearPrograma from "./pages/CrearPrograma"

function App() {
  return (
    <>
      <Router>
        <Routes>
          {/* ---- Rutas PÚBLICAS (CON Navbar) ---- */}
          {/* Todas las rutas dentro de este 'element' usarán MainLayout */}
          <Route element={<MainLayout />}>
            <Route path="/" element={<Home />} />
            <Route path="/nosotros" element={<Nosotros />} />
            {/* Añade aquí otras rutas que SÍ deban tener el Navbar */}
          </Route>
          {/* ---- Rutas de Autenticación (SIN Navbar) ---- */}

          {/* Estas rutas están fuera de MainLayout, por lo tanto no tendrán Navbar */}
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/onboarding" element={<Onboarding />} />

          {/* ---- RUTAS PROTEGIDAS (CON Navbar) ---- */}
          {/* 1. Están dentro de <MainLayout /> para tener el Navbar.
          2. Están dentro de <ProtectedRoute /> para estar seguras.*/}

          <Route element={<ProtectedRoute />}>
            {/* 3. Si la protección pasa, usa el DashboardLayout (Navbar oscuro) */}
            <Route element={<DashboardLayout />}>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/dashboard/perfil" element={<Perfil />} />
              {/* 4. Añade aquí OTRAS rutas que usen el navbar del dashboard */}
              <Route path="/dashboard/entrenamiento" element={<Entrenamiento />} />
              <Route path="/dashboard/crear-rutina" element={<CrearRutina />} />
              <Route path="/dashboard/crear-programa" element={<CrearPrograma />} />
              <Route path="/dashboard/seguimiento" element={<Seguimiento />} />
              <Route path="/dashboard/nutricion" element={<Nutricion />} />
              <Route path="/dashboard/comunidad" element={<Comunidad />} />
              <Route path="/dashboard/sesiones" element={<Sesiones />} />
              <Route path="/dashboard/reto-semanal" element={<RetoSemanal />} />
              <Route path="/dashboard/articulos" element={<Articulos />} />
              <Route path="/dashboard/ejercicios" element={<Ejercicios />} />
              <Route path="/dashboard/crear-entrenamiento" element={<CrearEntrenamiento />} />
            </Route>
          </Route>

          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
    </>
  )
}

export default App
