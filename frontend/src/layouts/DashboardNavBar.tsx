"use client"
import { Link, useNavigate } from "react-router-dom"
import { UserCircle, LogOut, PlusCircle } from "lucide-react"
import { useEffect, useState } from "react"

function DashboardNavbar() {
  const navigate = useNavigate()
  const [isTrainer, setIsTrainer] = useState(false)

  // Verificar rol al cargar el navbar
  useEffect(() => {
    const storedData = localStorage.getItem("userData")
    if (storedData) {
        const parsed = JSON.parse(storedData)
        // Comprobamos si es ROLE_TRAINER (formato backend) o trainer (formato legacy)
        if (parsed.role === "ROLE_TRAINER" || parsed.role === "trainer") {
            setIsTrainer(true)
        }
    }
  }, [])

  const handleLogout = () => {
    // CORREGIDO: Usamos las mismas claves que en Login.tsx y Perfil.tsx
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
    localStorage.removeItem("userData")
    localStorage.removeItem("onboardingCompleted")
    navigate("/login")
  }

  return (
    <nav className="bg-black text-white shadow-lg border-b border-yellow-500/20 sticky top-0 z-50 backdrop-blur-sm">
      <div className="container mx-auto px-6 py-4">
        <div className="flex justify-between items-center">
          {/* Left side: Logo and Navigation */}
          <div className="flex items-center space-x-6">
            <Link to="/dashboard" className="flex items-center space-x-3 group">
              <img
                src="/images/FitHub_Logo.png"
                alt="FitHub Logo"
                className="h-9 w-auto group-hover:scale-105 transition-transform"
                onError={(e) => {e.currentTarget.src = "https://via.placeholder.com/40x40?text=FH"}}
              />
              <span className="text-xl font-bold bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
                FitHub
              </span>
            </Link>

            <div className="hidden lg:flex space-x-1">
              <Link
                to="/dashboard/entrenamiento"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Entrenamiento
              </Link>
              {/* Nuevos Enlaces */}
              <Link
                to="/dashboard/mis-rutinas"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Rutinas
              </Link>
              <Link
                to="/dashboard/mis-programas"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Programas
              </Link>

              {/* MENU EXCLUSIVO DE ENTRENADOR */}
              {isTrainer && (
                  <Link
                    to="/dashboard/crear-entrenamiento"
                    className="py-2 px-3 rounded-lg bg-yellow-500/20 text-yellow-400 hover:bg-yellow-500 hover:text-black transition-colors text-sm font-bold flex items-center gap-1"
                  >
                    <PlusCircle className="w-4 h-4" />
                    Crear
                  </Link>
              )}

              <Link
                to="/dashboard/ejercicios"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Ejercicios
              </Link>
              <Link
                to="/dashboard/seguimiento"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Seguimiento
              </Link>
              <Link
                to="/dashboard/nutricion"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Nutrición
              </Link>
              <Link
                to="/dashboard/comunidad"
                className="py-2 px-3 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors text-sm font-medium"
              >
                Comunidad
              </Link>
            </div>
          </div>

          {/* Right side: Profile and Logout */}
          <div className="flex items-center space-x-4">
            <Link
              to="/dashboard/perfil"
              className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-lg hover:bg-yellow-500/10 hover:text-yellow-500 transition-colors group"
            >
              <span className="font-medium">Mi Perfil</span>
              <UserCircle className="h-6 w-6" />
            </Link>
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 bg-yellow-500 text-black font-semibold py-2 px-4 rounded-lg hover:bg-yellow-400 transition-all hover:shadow-lg hover:shadow-yellow-500/50"
            >
              <LogOut className="h-4 w-4" />
              <span className="hidden sm:inline">Cerrar Sesión</span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}

export default DashboardNavbar