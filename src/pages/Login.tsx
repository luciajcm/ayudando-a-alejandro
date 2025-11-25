import type React from "react"
import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Mail, Lock, ArrowRight, Loader2 } from "lucide-react"
import { GoogleLogin } from "@react-oauth/google" 
import { signIn, googleSignIn, getProfile } from "../api/api"
import { validateEmail } from "../utils/validation" // Importar validador

const Login: React.FC = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    email: "", 
    password: ""
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estado para error específico del email en Login
  const [emailError, setEmailError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (error) setError(null);
    if (e.target.name === "email") setEmailError(null);
  };

  // Login Normal (Email/Password)
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validación simple antes de conectar
    const emailVal = validateEmail(formData.email);
    if (emailVal) {
        setEmailError(emailVal);
        return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const data = await signIn(formData);

      // Guardar Tokens
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);
      
      // Guardar datos básicos (o pedir perfil completo si prefieres consistencia)
      // Opción rápida para login normal (que devuelve menos datos usualmente)
      const userData = { email: formData.email, role: 'LEARNER' }; 
      localStorage.setItem("userData", JSON.stringify(userData));
      
      // Nota: Si tu SignInResponse de Java no devuelve nombre, podrías hacer getProfile() aquí también
      // para asegurar que el dashboard tenga el nombre real.
      
      navigate("/dashboard");

    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || "Credenciales incorrectas");
    } finally {
      setIsLoading(false);
    }
  };

  // Login con Google (Éxito)
  const handleGoogleSuccess = async (credentialResponse: any) => {
    if (credentialResponse.credential) {
        setIsLoading(true);
        try {
            // 1. Login con Google
            const data = await googleSignIn(credentialResponse.credential);
            
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            
            // 2. Pedir los datos reales del usuario inmediatamente
            const userProfile = await getProfile(); 
            
            // 3. Guardar los datos REALES
            localStorage.setItem("userData", JSON.stringify(userProfile));
            
            navigate("/dashboard");
        } catch (err) {
            console.error(err);
            setError("Error al iniciar sesión con Google");
        } finally {
            setIsLoading(false);
        }
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left side - Form */}
      <div className="flex-1 flex flex-col justify-center items-center px-6 py-12 bg-white">
        <div className="w-full max-w-md">
          <Link to="/" className="inline-block mb-8 group">
            <h1 className="text-3xl font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">Fithub</h1>
          </Link>

          <div className="mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-3">Bienvenido de nuevo</h2>
            <p className="text-lg text-gray-600">Ingresa a tu cuenta para continuar</p>
          </div>

          {error && (
            <div className="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-red-600 text-sm font-medium animate-pulse">
              {error}
            </div>
          )}

          <form className="space-y-5" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="email" className="block text-sm font-semibold text-gray-900 mb-2">
                Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <Mail className={`h-5 w-5 ${emailError ? "text-red-400" : "text-gray-400"}`} />
                </div>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="tu@correo.com"
                  className={`w-full pl-12 pr-4 py-3.5 bg-gray-50 border rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 transition-all disabled:opacity-50 ${
                    emailError 
                    ? "border-red-500 focus:ring-red-200" 
                    : "border-gray-200 focus:ring-yellow-500"
                  }`}
                  required
                  disabled={isLoading}
                />
              </div>
              {emailError && <p className="text-red-500 text-xs mt-1 ml-1">{emailError}</p>}
            </div>

            <div>
              <div className="flex justify-between items-center mb-2">
                <label htmlFor="password" className="block text-sm font-semibold text-gray-900">
                  Contraseña
                </label>
                <Link
                  to="/forgot-password"
                  className="text-sm font-medium text-yellow-500 hover:text-yellow-600 transition-colors"
                >
                  ¿Olvidaste tu contraseña?
                </Link>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="••••••••••••"
                  className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all disabled:opacity-50"
                  required
                  disabled={isLoading}
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="group w-full mt-6 py-3.5 px-4 bg-yellow-500 text-black font-semibold rounded-xl text-lg hover:bg-yellow-600 transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-lg hover:shadow-yellow-500/25 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {isLoading ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  Ingresando...
                </>
              ) : (
                <>
                  Ingresar
                  <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                </>
              )}
            </button>
          </form>

          <div className="flex items-center my-8">
            <div className="flex-1 border-t border-gray-200"></div>
            <span className="px-4 text-sm font-medium text-gray-500">O continúa con</span>
            <div className="flex-1 border-t border-gray-200"></div>
          </div>

          {/* BOTÓN DE GOOGLE OFICIAL */}
          <div className="w-full flex justify-center">
            <GoogleLogin 
                onSuccess={handleGoogleSuccess}
                onError={() => setError("Error al conectar con Google")}
                type="standard"
                theme="outline"
                size="large"
                text="continue_with"
                shape="pill"
                width="350" 
            />
          </div>

          <div className="text-center mt-8">
            <p className="text-gray-600">
              ¿No tienes una cuenta?{" "}
              <Link to="/register" className="font-semibold text-yellow-500 hover:text-yellow-600 transition-colors">
                Regístrate gratis
              </Link>
            </p>
          </div>
        </div>
      </div>

      {/* Right side - Visual */}
      <div className="hidden lg:flex flex-1 bg-gradient-to-br from-gray-900 via-gray-800 to-black relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSJyZ2JhKDI1NSwyNTUsMjU1LDAuMDUpIiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=')] opacity-20"></div>
        <div className="relative z-10 flex flex-col justify-center px-16 text-white">
          <h2 className="text-5xl font-bold mb-6 leading-tight text-balance">
            Tu próximo nivel <span className="text-yellow-500">te espera</span>
          </h2>
          <p className="text-xl text-gray-300 mb-8 leading-relaxed text-pretty max-w-lg">
            Accede a tu panel personalizado, conecta con tu entrenador y sigue tu progreso en tiempo real.
          </p>
          {/* Feature list visual */}
           <div className="space-y-4 max-w-md">
            <div className="flex items-start gap-4">
              <div className="w-10 h-10 rounded-lg bg-yellow-500/20 flex items-center justify-center flex-shrink-0">
                <svg className="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1">Planes Personalizados</h3>
                <p className="text-gray-400">Rutinas adaptadas a tus objetivos</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="w-10 h-10 rounded-lg bg-yellow-500/20 flex items-center justify-center flex-shrink-0">
                <svg className="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1">Seguimiento en Tiempo Real</h3>
                <p className="text-gray-400">Visualiza tu progreso al instante</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="w-10 h-10 rounded-lg bg-yellow-500/20 flex items-center justify-center flex-shrink-0">
                <svg className="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" /></svg>
              </div>
              <div>
                <h3 className="font-semibold text-lg mb-1">Comunidad Activa</h3>
                <p className="text-gray-400">Conecta con otros atletas</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login