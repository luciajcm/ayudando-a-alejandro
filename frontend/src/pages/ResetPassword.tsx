import type React from "react"
import { useState, useEffect } from "react"
import { Link, useNavigate, useSearchParams } from "react-router-dom" // Importar useSearchParams
import { Lock, Eye, EyeOff, CheckCircle2, Loader2, AlertTriangle } from "lucide-react"
import { resetPassword } from "../api/api" // Importar función de API
import { validatePassword } from "../utils/validation" // Importar validación

const ResetPassword: React.FC = () => {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  
  // 1. Obtener el token de la URL (ej: ?token=xyz...)
  const token = searchParams.get("token")

  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  
  // Estados para la lógica de red
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle")
  const [message, setMessage] = useState("")

  // Verificar si hay token al cargar
  useEffect(() => {
    if (!token) {
        setStatus("error");
        setMessage("Enlace inválido o incompleto. Por favor, solicita uno nuevo.");
    }
  }, [token]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    // Validaciones locales
    if (password !== confirmPassword) {
      setStatus("error")
      setMessage("Las contraseñas no coinciden")
      return
    }
    
    const passError = validatePassword(password);
    if (passError) {
        setStatus("error");
        setMessage(passError);
        return;
    }

    if (!token) return;

    setIsLoading(true)
    setStatus("idle")
    
    try {
      // 2. Llamada al Backend
      await resetPassword(token, password)
      setStatus("success")
      // No redirigimos automáticamente para que el usuario vea el mensaje de éxito
    } catch (error: any) {
      setStatus("error")
      setMessage(error.response?.data?.message || "El enlace ha expirado o es inválido.")
    } finally {
      setIsLoading(false)
    }
  }

  const passwordStrength = password.length >= 8 ? "strong" : password.length >= 6 ? "medium" : "weak"

  // 3. UI de Éxito (Reemplaza el formulario si todo salió bien)
  if (status === "success") {
      return (
        <div className="min-h-screen flex items-center justify-center bg-white px-6">
            <div className="max-w-md w-full text-center">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                    <CheckCircle2 className="w-8 h-8 text-green-600" />
                </div>
                <h2 className="text-3xl font-bold text-gray-900 mb-4">¡Contraseña Actualizada!</h2>
                <p className="text-gray-600 mb-8">Tu contraseña ha sido restablecida exitosamente. Ya puedes acceder a tu cuenta.</p>
                <Link 
                    to="/login"
                    className="block w-full py-3.5 px-4 bg-yellow-500 text-black font-semibold rounded-xl text-lg hover:bg-yellow-600 transition-all text-center"
                >
                    Iniciar Sesión
                </Link>
            </div>
        </div>
      )
  }

  return (
    <div className="min-h-screen flex">
      {/* Left side - Visual */}
      <div className="hidden lg:flex flex-1 bg-gradient-to-br from-yellow-600 via-yellow-500 to-yellow-400 relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0icmdiYSgyNTUsMjU1LDI1NSwwLjEpIiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=')] opacity-20"></div>

        <div className="relative z-10 flex flex-col justify-center px-16 text-white">
          <div className="max-w-lg">
            <div className="w-16 h-16 bg-white/20 backdrop-blur-sm rounded-2xl flex items-center justify-center mb-8 border border-white/30">
              <Lock className="w-8 h-8" />
            </div>
            <h2 className="text-5xl font-bold mb-6 leading-tight text-balance">
              Nueva contraseña <span className="text-gray-900">segura</span>
            </h2>
            <p className="text-xl text-yellow-50 leading-relaxed text-pretty">
              Crea una contraseña fuerte y única para proteger tu cuenta.
            </p>

            <div className="mt-12 space-y-4">
              <div className="flex items-start gap-3">
                <CheckCircle2 className={`w-5 h-5 mt-0.5 flex-shrink-0 ${password.length >= 8 ? "text-green-400" : "text-white"}`} />
                <p className="text-yellow-50">Al menos 8 caracteres</p>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircle2 className={`w-5 h-5 mt-0.5 flex-shrink-0 ${/[A-Z]/.test(password) && /[a-z]/.test(password) ? "text-green-400" : "text-white"}`} />
                <p className="text-yellow-50">Incluye mayúsculas y minúsculas</p>
              </div>
              <div className="flex items-start gap-3">
                <CheckCircle2 className={`w-5 h-5 mt-0.5 flex-shrink-0 ${/\d/.test(password) && /[@$!%*?&]/.test(password) ? "text-green-400" : "text-white"}`} />
                <p className="text-yellow-50">Incluye números y símbolos</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Right side - Form */}
      <div className="flex-1 flex flex-col justify-center items-center px-6 py-12 bg-white">
        <div className="w-full max-w-md">
          <Link to="/" className="inline-block mb-8 group">
            <h1 className="text-3xl font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">Fithub</h1>
          </Link>

          <div className="mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-3">Restablecer contraseña</h2>
            <p className="text-lg text-gray-600">Ingresa tu nueva contraseña</p>
          </div>

          {/* Mensaje de Error */}
          {status === "error" && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700 animate-pulse">
                <AlertTriangle className="w-5 h-5 mt-0.5 flex-shrink-0" />
                <p className="text-sm font-medium">{message}</p>
            </div>
          )}

          {!token ? (
             <div className="text-center">
                 <p className="text-gray-600 mb-4">No se encontró un código válido en el enlace.</p>
                 <Link to="/forgot-password" className="text-yellow-600 font-semibold hover:underline">Solicitar nuevo enlace</Link>
             </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Nueva Contraseña */}
                <div>
                <label htmlFor="password" className="block text-sm font-semibold text-gray-900 mb-2">
                    Nueva contraseña
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <Lock className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                    type={showPassword ? "text" : "password"}
                    id="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full pl-12 pr-12 py-3 bg-gray-50 border border-gray-200 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all"
                    required
                    />
                    <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-400 hover:text-gray-600"
                    >
                    {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                    </button>
                </div>

                {/* Indicador de Fuerza Visual */}
                {password && (
                    <div className="mt-2">
                    <div className="flex gap-1 mb-1">
                        <div className={`h-1 flex-1 rounded-full ${passwordStrength === "weak" ? "bg-red-500" : "bg-gray-200"}`}></div>
                        <div className={`h-1 flex-1 rounded-full ${passwordStrength === "medium" || passwordStrength === "strong" ? "bg-yellow-500" : "bg-gray-200"}`}></div>
                        <div className={`h-1 flex-1 rounded-full ${passwordStrength === "strong" ? "bg-green-500" : "bg-gray-200"}`}></div>
                    </div>
                    </div>
                )}
                </div>

                {/* Confirmar Contraseña */}
                <div>
                <label htmlFor="confirmPassword" className="block text-sm font-semibold text-gray-900 mb-2">
                    Confirmar contraseña
                </label>
                <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                    <Lock className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                    type={showConfirmPassword ? "text" : "password"}
                    id="confirmPassword"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full pl-12 pr-12 py-3 bg-gray-50 border border-gray-200 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all"
                    required
                    />
                    <button
                    type="button"
                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                    className="absolute inset-y-0 right-0 pr-4 flex items-center text-gray-400 hover:text-gray-600"
                    >
                    {showConfirmPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                    </button>
                </div>
                </div>

                <button
                type="submit"
                disabled={isLoading}
                className="w-full mt-6 py-3.5 px-4 bg-yellow-500 text-black font-semibold rounded-xl text-lg hover:bg-yellow-600 transition-all duration-300 hover:shadow-lg hover:shadow-yellow-500/25 disabled:opacity-70 disabled:cursor-not-allowed flex justify-center items-center gap-2"
                >
                {isLoading ? <><Loader2 className="w-5 h-5 animate-spin"/> Actualizando...</> : "Restablecer contraseña"}
                </button>
            </form>
          )}
          
          <div className="text-center mt-8">
            <Link to="/login" className="text-gray-600 hover:text-gray-900 transition-colors">
              Volver al inicio de sesión
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ResetPassword