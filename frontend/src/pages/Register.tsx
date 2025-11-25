import type React from "react"
import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Mail, Lock, User, Phone, ArrowRight, Loader2 } from "lucide-react"
import { GoogleLogin } from "@react-oauth/google"
import { googleSignIn, getProfile } from "../api/api"
import { validateEmail, validatePassword, validatePhone } from "../utils/validation"

export interface RegisterData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
}

const Register: React.FC = () => {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Estado para errores de validación local (campo por campo)
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const [formData, setFormData] = useState<RegisterData>({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: ""
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    
    // Limpiar el error específico cuando el usuario empieza a corregirlo
    if (errors[name]) {
      setErrors({ ...errors, [name]: "" });
    }
    setError(null); // Limpiar error general
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // --- VALIDACIÓN ANTES DE ENVIAR ---
    const newErrors: { [key: string]: string } = {};

    // 1. Nombres
    if (!formData.firstName.trim()) newErrors.firstName = "El nombre es obligatorio";
    if (!formData.lastName.trim()) newErrors.lastName = "El apellido es obligatorio";

    // 2. Email
    const emailVal = validateEmail(formData.email);
    if (emailVal) newErrors.email = emailVal;

    // 3. Teléfono
    const phoneVal = validatePhone(formData.phone);
    if (phoneVal) newErrors.phone = phoneVal;

    // 4. Contraseña (Validación estricta del backend)
    const passVal = validatePassword(formData.password);
    if (passVal) newErrors.password = passVal;

    // Si hay errores, los mostramos y detenemos el proceso
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Si todo es válido, vamos al Onboarding
    navigate("/onboarding", { state: formData });
  }

  const handleGoogleSuccess = async (credentialResponse: any) => {
    if (credentialResponse.credential) {
        setIsLoading(true);
        setError(null);
        try {
            console.log("Google Register JWT recibido...");
            const data = await googleSignIn(credentialResponse.credential);
            
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            
            // Obtener perfil real inmediatamente
            const userProfile = await getProfile();
            localStorage.setItem("userData", JSON.stringify(userProfile));
            
            navigate("/dashboard");

        } catch (err: any) {
            console.error("Error Google Register:", err);
            setError("Error al registrarse con Google");
        } finally {
            setIsLoading(false);
        }
    }
  };

  // Clase auxiliar para input con error (borde rojo)
  const getInputClass = (fieldName: string) => `
    w-full pl-12 pr-4 py-3 bg-gray-50 border rounded-xl text-gray-900 
    placeholder-gray-500 focus:outline-none focus:ring-2 transition-all
    ${errors[fieldName] 
      ? "border-red-500 focus:ring-red-200 focus:border-red-500" 
      : "border-gray-200 focus:ring-yellow-500 focus:border-transparent"
    }
  `;

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex flex-1 bg-gradient-to-br from-yellow-600 via-yellow-500 to-yellow-400 relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSJyZ2JhKDI1NSwyNTUsMjU1LDAuMSkiIHN0cm9rZS13aWR0aD0iMSIvPjwvcGF0dGVybj48L2RlZnM+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0idXJsKCNncmlkKSIvPjwvc3ZnPg==')] opacity-20"></div>
        <div className="relative z-10 flex flex-col justify-center px-16 text-white">
          <h2 className="text-5xl font-bold mb-6 leading-tight text-balance">
            Empieza tu transformación <span className="text-gray-900">hoy</span>
          </h2>
          <p className="text-xl text-yellow-50 mb-12 leading-relaxed text-pretty max-w-lg">
            Únete a miles de personas que ya están alcanzando sus metas fitness con Fithub.
          </p>
           <div className="grid grid-cols-2 gap-6 max-w-md">
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
              <div className="text-4xl font-bold mb-2">1000+</div>
              <div className="text-yellow-50 text-sm">Entrenadores certificados</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
              <div className="text-4xl font-bold mb-2">50K+</div>
              <div className="text-yellow-50 text-sm">Usuarios activos</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
              <div className="text-4xl font-bold mb-2">95%</div>
              <div className="text-yellow-50 text-sm">Tasa de satisfacción</div>
            </div>
            <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 border border-white/20">
              <div className="text-4xl font-bold mb-2">24/7</div>
              <div className="text-yellow-50 text-sm">Soporte disponible</div>
            </div>
          </div>
        </div>
      </div>

      <div className="flex-1 flex flex-col justify-center items-center px-6 py-12 bg-white">
        <div className="w-full max-w-md">
          <Link to="/" className="inline-block mb-8 group">
            <h1 className="text-3xl font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">Fithub</h1>
          </Link>

          <div className="mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-3">Crea tu cuenta</h2>
            <p className="text-lg text-gray-600">Comienza tu viaje fitness ahora</p>
          </div>

          {error && (
            <div className="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-red-600 text-sm font-medium">
              {error}
            </div>
          )}

          {isLoading ? (
             <div className="flex justify-center py-10">
                <Loader2 className="w-10 h-10 animate-spin text-yellow-500" />
             </div>
          ) : (
            <>
              <form className="space-y-4" onSubmit={handleSubmit}>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="firstName" className="block text-sm font-semibold text-gray-900 mb-2">Nombre</label>
                    <div className="relative">
                      <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <User className={`h-5 w-5 ${errors.firstName ? "text-red-400" : "text-gray-400"}`} />
                      </div>
                      <input type="text" id="firstName" name="firstName" value={formData.firstName} onChange={handleChange} placeholder="Tu nombre" className={getInputClass('firstName')} />
                    </div>
                    {errors.firstName && <p className="text-red-500 text-xs mt-1 ml-1">{errors.firstName}</p>}
                  </div>
                  <div>
                    <label htmlFor="lastName" className="block text-sm font-semibold text-gray-900 mb-2">Apellido</label>
                    <input type="text" id="lastName" name="lastName" value={formData.lastName} onChange={handleChange} placeholder="Tu apellido" className={`px-4 ${getInputClass('lastName')}`} />
                    {errors.lastName && <p className="text-red-500 text-xs mt-1 ml-1">{errors.lastName}</p>}
                  </div>
                </div>

                <div>
                  <label htmlFor="email" className="block text-sm font-semibold text-gray-900 mb-2">Email</label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Mail className={`h-5 w-5 ${errors.email ? "text-red-400" : "text-gray-400"}`} />
                    </div>
                    <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} placeholder="tu@correo.com" className={getInputClass('email')} />
                  </div>
                  {errors.email && <p className="text-red-500 text-xs mt-1 ml-1">{errors.email}</p>}
                </div>

                <div>
                  <label htmlFor="phone" className="block text-sm font-semibold text-gray-900 mb-2">Teléfono</label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Phone className={`h-5 w-5 ${errors.phone ? "text-red-400" : "text-gray-400"}`} />
                    </div>
                    <input type="tel" id="phone" name="phone" value={formData.phone} onChange={handleChange} placeholder="987654321" className={getInputClass('phone')} />
                  </div>
                  {errors.phone && <p className="text-red-500 text-xs mt-1 ml-1">{errors.phone}</p>}
                </div>

                <div>
                  <label htmlFor="password" className="block text-sm font-semibold text-gray-900 mb-2">Contraseña</label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Lock className={`h-5 w-5 ${errors.password ? "text-red-400" : "text-gray-400"}`} />
                    </div>
                    <input type="password" id="password" name="password" value={formData.password} onChange={handleChange} placeholder="••••••••" className={getInputClass('password')} />
                  </div>
                  {errors.password && <p className="text-red-500 text-xs mt-1 ml-1">{errors.password}</p>}
                </div>

                <button type="submit" className="group w-full mt-6 py-3.5 px-4 bg-yellow-500 text-black font-semibold rounded-xl text-lg hover:bg-yellow-600 transition-all duration-300 flex items-center justify-center gap-2 hover:shadow-lg hover:shadow-yellow-500/25">
                  Crear Cuenta
                  <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                </button>
              </form>

              <div className="flex items-center my-6">
                <div className="flex-1 border-t border-gray-200"></div>
                <span className="px-4 text-sm font-medium text-gray-500">O regístrate con</span>
                <div className="flex-1 border-t border-gray-200"></div>
              </div>

              <div className="w-full flex justify-center">
                <GoogleLogin
                    onSuccess={handleGoogleSuccess}
                    onError={() => setError("Error al registrarse con Google")}
                    type="standard"
                    theme="outline"
                    size="large"
                    text="signup_with" 
                    shape="pill"
                    width="350" 
                />
              </div>
            </>
          )}

          <div className="text-center mt-8">
            <p className="text-gray-600">
              ¿Ya tienes una cuenta?{" "}
              <Link to="/login" className="font-semibold text-yellow-500 hover:text-yellow-600 transition-colors">
                Inicia sesión
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Register