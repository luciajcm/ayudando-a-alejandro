import type React from "react"
import { useState, useEffect } from "react"
import { useNavigate, useLocation } from "react-router-dom"
import { ChevronRight, ChevronLeft, Sparkles, User, Calendar, Weight, Ruler, Target, Activity, Loader2 } from "lucide-react"
import type { RegisterData } from "./Register"
// IMPORTANTE: Asegúrate de tener esta función en tu api.ts
import { updateProfile } from "../api/api" 

// URL Base de tu API
const API_URL = "http://localhost:8080/api/auth";

interface OnboardingData {
  gender: string
  age: number
  weight: number
  height: number
  goal: string
  activityLevel: string
  username: string
  firstName: string
  lastName: string
}

const Onboarding: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  
  // --- LÓGICA PASO 4: DETECCIÓN DE MODO (GOOGLE vs REGISTRO) ---
  const [isUpdateMode, setIsUpdateMode] = useState(false);
  const [googleUserData, setGoogleUserData] = useState<any>(null);
  
  // Recuperamos el state. Puede ser RegisterData (normal) o { googleUser, isUpdateMode } (Google)
  const locationState = location.state as any;

  const [currentSlide, setCurrentSlide] = useState(0)
  const [isLoading, setIsLoading] = useState(false)
  
  const [formData, setFormData] = useState<OnboardingData>({
    gender: "",
    age: 25,
    weight: 70,
    height: 170,
    goal: "",
    activityLevel: "",
    username: "",
    firstName: "",
    lastName: "",
  })

  useEffect(() => {
    // Validación de seguridad
    if (!locationState) {
      navigate("/register", { replace: true });
      return;
    }

    // ESCENARIO 1: Usuario de Google que necesita completar perfil
    if (locationState.isUpdateMode && locationState.googleUser) {
        setIsUpdateMode(true);
        setGoogleUserData(locationState.googleUser);
        
        setFormData(prev => ({
            ...prev,
            firstName: locationState.googleUser.firstName || "",
            // Si el apellido es el punto dummy ".", lo dejamos vacío para que el usuario lo corrija
            lastName: locationState.googleUser.lastName === "." ? "" : locationState.googleUser.lastName,
            username: locationState.googleUser.username || ""
        }));
    } 
    // ESCENARIO 2: Usuario de Registro Normal
    else if (locationState.email) {
        setFormData(prev => ({
            ...prev,
            firstName: locationState.firstName || "",
            lastName: locationState.lastName || ""
        }));
    }
  }, [locationState, navigate]);

  const totalSlides = 8

  const nextSlide = () => {
    if (currentSlide < totalSlides - 1) {
      setCurrentSlide(currentSlide + 1)
    }
  }

  const prevSlide = () => {
    if (currentSlide > 0) {
      setCurrentSlide(currentSlide - 1)
    }
  }

  // --- LÓGICA PASO 4: MANEJO HÍBRIDO DE FINALIZACIÓN ---
  const handleFinish = async () => {
    setIsLoading(true);

    try {
      // Cálculo de fecha de nacimiento
      const currentYear = new Date().getFullYear();
      const birthYear = currentYear - formData.age;
      const birthday = `${birthYear}-01-01`; 

      if (isUpdateMode) {
          // =========================================================
          // MODO ACTUALIZACIÓN (Usuario de Google) - Hace PUT
          // =========================================================
          const updatePayload = {
            firstName: formData.firstName,
            lastName: formData.lastName,
            username: formData.username,
            birthday: birthday,
            gender: formData.gender,
            height: formData.height / 100, // Metros
            weight: Number(formData.weight),
            phoneNumber: googleUserData.phoneNumber, // Mantenemos el dummy o pedimos uno nuevo
            userStatus: "AVAILABLE"
          };

          console.log("Actualizando perfil Google...", updatePayload);
          
          // Llamada a la API para actualizar (PUT /users/{id})
          await updateProfile(googleUserData.id, updatePayload);

          // Actualizamos el localStorage con los datos nuevos reales
          const currentData = JSON.parse(localStorage.getItem("userData") || "{}");
          localStorage.setItem("userData", JSON.stringify({ ...currentData, ...updatePayload }));

      } else {
          // =========================================================
          // MODO REGISTRO (Usuario Normal) - Hace POST /signup
          // =========================================================
          const cleanPhone = locationState.phone 
              ? locationState.phone.replace(/\s+/g, '').replace(/-/g, '') 
              : "";

          const signupPayload = {
            firstName: formData.firstName, 
            lastName: formData.lastName,
            username: formData.username,
            email: locationState.email,
            password: locationState.password,
            phoneNumber: cleanPhone, 
            birthday: birthday,
            gender: formData.gender, 
            role: "LEARNER", 
            height: formData.height / 100,
            weight: Number(formData.weight)
          };

          console.log("Creando cuenta nueva...", signupPayload);

          const response = await fetch(`${API_URL}/signup`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(signupPayload)
          });

          const data = await response.json();

          if (!response.ok) {
            throw new Error(data.message || "Error al crear la cuenta");
          }

          // Guardar tokens
          if (data.accessToken) {
              localStorage.setItem("accessToken", data.accessToken);
              if(data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
              
              localStorage.setItem("userData", JSON.stringify({
                firstName: signupPayload.firstName,
                lastName: signupPayload.lastName,
                role: "LEARNER",
                username: signupPayload.username
              }));
          }
      }
      
      // Fin común para ambos casos
      localStorage.setItem("onboardingCompleted", "true");
      navigate("/dashboard");

    } catch (error: any) {
      console.error("Error completo:", error);
      alert(`Error: ${error.message}`);
    } finally {
      setIsLoading(false);
    }
  }

  const canContinue = () => {
    switch (currentSlide) {
      case 0: return true
      case 1: return formData.gender !== ""
      case 2: return formData.age > 0
      case 3: return formData.weight > 0
      case 4: return formData.height > 0
      case 5: return formData.goal !== ""
      case 6: return formData.activityLevel !== ""
      case 7: return formData.username !== "" && formData.firstName !== "" && formData.lastName !== ""
      default: return false
    }
  }

  // Si no hay estado, no renderizar (el useEffect redirigirá)
  if (!locationState) return null;

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 flex items-center justify-center p-6 relative overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0icmdiYSgyNTUsMjU1LDI1NSwwLjAzKSIgc3Ryb2tlLXdpZHRoPSIxIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2dyaWQpIi8+PC9zdmc+')] opacity-40"></div>

      <div className="relative z-10 w-full max-w-2xl">
        {/* Progress Bar */}
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm font-medium text-gray-400">
              Paso {currentSlide + 1} de {totalSlides}
            </span>
            <span className="text-sm font-medium text-gray-400">
              {Math.round(((currentSlide + 1) / totalSlides) * 100)}%
            </span>
          </div>
          <div className="w-full h-2 bg-gray-700 rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-yellow-500 to-yellow-400 transition-all duration-300"
              style={{ width: `${((currentSlide + 1) / totalSlides) * 100}%` }}
            ></div>
          </div>
        </div>

        {/* Slides Container */}
        <div className="bg-gray-800/50 backdrop-blur-sm rounded-3xl border border-gray-700/50 p-8 md:p-12 min-h-[500px] flex flex-col">
          
          {/* Slide 1: Bienvenida */}
          {currentSlide === 0 && (
            <div className="flex-1 flex flex-col items-center justify-center text-center">
              <div className="w-24 h-24 bg-gradient-to-br from-yellow-500 to-yellow-400 rounded-full flex items-center justify-center mb-6 animate-pulse">
                <Sparkles className="w-12 h-12 text-black" />
              </div>
              <h2 className="text-5xl font-bold text-white mb-4">
                {/* Texto Dinámico según modo */}
                {isUpdateMode ? "¡Completa tu perfil!" : "¡Bienvenido a Fithub!"}
              </h2>
              <p className="text-xl text-gray-300 max-w-md text-pretty">
                {isUpdateMode 
                    ? `Hola ${formData.firstName}, necesitamos unos datos extra para personalizar tu plan.`
                    : `Hola ${formData.firstName}, estás a punto de comenzar un viaje increíble.`
                }
              </p>
              <div className="mt-8 p-6 bg-yellow-500/10 border border-yellow-500/20 rounded-2xl">
                <p className="text-yellow-400 font-medium">
                  "El primer paso es siempre el más importante. ¡Estamos contigo!"
                </p>
              </div>
            </div>
          )}

          {/* Slide 2: Género */}
          {currentSlide === 1 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <User className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Cuál es tu género?</h2>
              </div>
              <div className="grid grid-cols-2 gap-4 max-w-md mx-auto w-full">
                <button type="button" onClick={() => setFormData({ ...formData, gender: "MALE" })} className={`p-8 rounded-2xl border-2 transition-all duration-300 ${formData.gender === "MALE" ? "bg-yellow-500 border-yellow-400 shadow-lg shadow-yellow-500/25 text-black" : "bg-gray-700/30 border-gray-600 hover:border-gray-500"}`}>
                  <div className="text-5xl mb-3">👨</div>
                  <p className="text-lg font-semibold text-white">Masculino</p>
                </button>
                <button type="button" onClick={() => setFormData({ ...formData, gender: "FEMALE" })} className={`p-8 rounded-2xl border-2 transition-all duration-300 ${formData.gender === "FEMALE" ? "bg-yellow-500 border-yellow-400 shadow-lg shadow-yellow-500/25 text-black" : "bg-gray-700/30 border-gray-600 hover:border-gray-500"}`}>
                  <div className="text-5xl mb-3">👩</div>
                  <p className="text-lg font-semibold text-white">Femenino</p>
                </button>
              </div>
            </div>
          )}

          {/* Slide 3: Edad */}
          {currentSlide === 2 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Cuántos años tienes?</h2>
              </div>
              <div className="flex-1 flex flex-col items-center justify-center">
                <div className="relative w-full max-w-md">
                  <div className="text-center mb-8">
                    <div className="text-7xl font-bold text-white mb-2">{formData.age}</div>
                    <p className="text-gray-400">años</p>
                  </div>
                  <input type="range" min="13" max="100" value={formData.age} onChange={(e) => setFormData({ ...formData, age: Number.parseInt(e.target.value) })} className="w-full h-3 bg-gray-700 rounded-full appearance-none cursor-pointer accent-yellow-500" />
                  <div className="flex justify-between mt-4 text-sm text-gray-500">
                    <span>13</span><span>100</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Slide 4: Peso */}
          {currentSlide === 3 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <Weight className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Cuál es tu peso?</h2>
              </div>
              <div className="flex-1 flex flex-col items-center justify-center">
                <div className="relative w-full max-w-md">
                  <div className="text-center mb-8">
                    <div className="text-7xl font-bold text-white mb-2">{formData.weight}</div>
                    <p className="text-gray-400">kilogramos</p>
                  </div>
                  <input type="range" min="30" max="200" value={formData.weight} onChange={(e) => setFormData({ ...formData, weight: Number.parseInt(e.target.value) })} className="w-full h-3 bg-gray-700 rounded-full appearance-none cursor-pointer accent-yellow-500" />
                  <div className="flex justify-between mt-4 text-sm text-gray-500">
                    <span>30 kg</span><span>200 kg</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Slide 5: Altura */}
          {currentSlide === 4 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <Ruler className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Cuál es tu altura?</h2>
              </div>
              <div className="flex-1 flex flex-col items-center justify-center">
                <div className="relative w-full max-w-md">
                  <div className="text-center mb-8">
                    <div className="text-7xl font-bold text-white mb-2">{formData.height}</div>
                    <p className="text-gray-400">centímetros</p>
                  </div>
                  <input type="range" min="120" max="220" value={formData.height} onChange={(e) => setFormData({ ...formData, height: Number.parseInt(e.target.value) })} className="w-full h-3 bg-gray-700 rounded-full appearance-none cursor-pointer accent-yellow-500" />
                  <div className="flex justify-between mt-4 text-sm text-gray-500">
                    <span>120 cm</span><span>220 cm</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Slide 6: Objetivo */}
          {currentSlide === 5 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <Target className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Cuál es tu objetivo?</h2>
              </div>
              <div className="grid grid-cols-2 gap-4">
                {[
                  { id: "lose-weight", label: "Perder peso", emoji: "🔥" },
                  { id: "gain-muscle", label: "Ganar masa muscular", emoji: "💪" },
                  { id: "endurance", label: "Resistencia", emoji: "🏃" },
                  { id: "gain-weight", label: "Aumentar peso", emoji: "📈" },
                  { id: "health", label: "Salud general", emoji: "❤️" },
                  { id: "other", label: "Otro", emoji: "🎯" },
                ].map((goal) => (
                  <button key={goal.id} type="button" onClick={() => setFormData({ ...formData, goal: goal.id })} className={`p-6 rounded-2xl border-2 transition-all duration-300 ${formData.goal === goal.id ? "bg-yellow-500 border-yellow-400 shadow-lg shadow-yellow-500/25 text-black" : "bg-gray-700/30 border-gray-600 hover:border-gray-500"}`}>
                    <div className="text-4xl mb-2">{goal.emoji}</div>
                    <p className="text-sm font-semibold text-white">{goal.label}</p>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Slide 7: Actividad */}
          {currentSlide === 6 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <Activity className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">¿Nivel de actividad?</h2>
              </div>
              <div className="space-y-4 max-w-md mx-auto w-full">
                {[
                  { id: "beginner", label: "Principiante", description: "Poco o ningún ejercicio", emoji: "🌱" },
                  { id: "intermediate", label: "Intermedio", description: "Ejercicio 3-4 veces por semana", emoji: "🏋️" },
                  { id: "advanced", label: "Avanzado", description: "Ejercicio 5+ veces por semana", emoji: "🏆" },
                ].map((level) => (
                  <button key={level.id} type="button" onClick={() => setFormData({ ...formData, activityLevel: level.id })} className={`w-full p-6 rounded-2xl border-2 transition-all duration-300 text-left ${formData.activityLevel === level.id ? "bg-yellow-500 border-yellow-400 shadow-lg shadow-yellow-500/25 text-black" : "bg-gray-700/30 border-gray-600 hover:border-gray-500"}`}>
                    <div className="flex items-center gap-4">
                      <div className="text-4xl">{level.emoji}</div>
                      <div>
                        <p className="text-lg font-semibold text-white">{level.label}</p>
                        <p className="text-sm text-gray-300">{level.description}</p>
                      </div>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Slide 8: Username */}
          {currentSlide === 7 && (
            <div className="flex-1 flex flex-col">
              <div className="flex items-center gap-3 mb-8">
                <div className="w-12 h-12 bg-yellow-500/20 rounded-xl flex items-center justify-center">
                  <User className="w-6 h-6 text-yellow-400" />
                </div>
                <h2 className="text-3xl font-bold text-white">Elige tu nombre de usuario</h2>
              </div>
              <div className="space-y-6 max-w-md mx-auto w-full">
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-2">Nombre de usuario</label>
                  <input type="text" value={formData.username} onChange={(e) => setFormData({ ...formData, username: e.target.value })} placeholder="tu_username" className="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all" />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Nombre</label>
                    <input type="text" value={formData.firstName} onChange={(e) => setFormData({ ...formData, firstName: e.target.value })} className="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-all" />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-gray-300 mb-2">Apellido</label>
                    <input type="text" value={formData.lastName} onChange={(e) => setFormData({ ...formData, lastName: e.target.value })} className="w-full px-4 py-3 bg-gray-700/50 border border-gray-600 rounded-xl text-white focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-all" />
                  </div>
                </div>
                <div className="mt-8 p-6 bg-yellow-500/10 border border-yellow-500/20 rounded-2xl">
                  <p className="text-yellow-400 text-center">¡Ya casi terminamos! Estás a un paso de empezar.</p>
                </div>
              </div>
            </div>
          )}

          {/* Navigation Buttons */}
          <div className="flex justify-between items-center mt-8 pt-8 border-t border-gray-700">
            <button type="button" onClick={prevSlide} disabled={currentSlide === 0 || isLoading} className="flex items-center gap-2 px-6 py-3 text-white hover:text-yellow-400 transition-colors disabled:opacity-30 disabled:cursor-not-allowed">
              <ChevronLeft className="w-5 h-5" /> Anterior
            </button>

            {currentSlide === totalSlides - 1 ? (
              <button type="button" onClick={handleFinish} disabled={!canContinue() || isLoading} className="px-8 py-3 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-600 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-lg hover:shadow-yellow-500/25 flex items-center gap-2">
                {isLoading ? <><Loader2 className="w-5 h-5 animate-spin" /> {isUpdateMode ? "Guardando..." : "Creando cuenta..."}</> : "Finalizar"}
              </button>
            ) : (
              <button type="button" onClick={nextSlide} disabled={!canContinue()} className="flex items-center gap-2 px-6 py-3 bg-yellow-500 text-black font-semibold rounded-xl hover:bg-yellow-600 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-lg hover:shadow-yellow-500/25">
                Siguiente <ChevronRight className="w-5 h-5" />
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Onboarding