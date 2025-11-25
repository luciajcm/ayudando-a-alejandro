import type React from "react"
import { useState } from "react"
import { Link } from "react-router-dom"
import { Mail, ArrowLeft, ArrowRight, Loader2, CheckCircle, AlertCircle } from "lucide-react"
import { requestPasswordReset } from "../api/api"
import { validateEmail } from "../utils/validation"

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle")
  const [errorMessage, setErrorMessage] = useState("")

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    // Validación básica
    const emailError = validateEmail(email);
    if (emailError) {
        setErrorMessage(emailError);
        setStatus("error");
        return;
    }

    setIsLoading(true)
    setStatus("idle")
    setErrorMessage("")

    try {
      await requestPasswordReset(email)
      setStatus("success")
    } catch (error: any) {
      setStatus("error")
      // Mensaje amigable si falla
      setErrorMessage("No pudimos enviar el correo. Verifica que la dirección sea correcta.")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex">
      {/* Left side - Form */}
      <div className="flex-1 flex flex-col justify-center items-center px-6 py-12 bg-white">
        <div className="w-full max-w-md">
          {/* Back button */}
          <Link
            to="/login"
            className="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-8 transition-colors group"
          >
            <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
            Volver al inicio de sesión
          </Link>

          {/* Logo */}
          <Link to="/" className="inline-block mb-8 group">
            <h1 className="text-3xl font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">Fithub</h1>
          </Link>

          {/* UI de Éxito */}
          {status === "success" ? (
            <div className="text-center animate-in fade-in slide-in-from-bottom-4 duration-500">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                    <CheckCircle className="w-8 h-8 text-green-600" />
                </div>
                <h2 className="text-3xl font-bold text-gray-900 mb-3">¡Correo enviado!</h2>
                <p className="text-lg text-gray-600 mb-8">
                    Hemos enviado las instrucciones a <strong>{email}</strong>. Revisa tu bandeja de entrada (y spam).
                </p>
                <Link 
                    to="/login"
                    className="block w-full py-3.5 px-4 bg-yellow-500 text-black font-semibold rounded-xl text-lg hover:bg-yellow-600 transition-all text-center"
                >
                    Volver al Login
                </Link>
            </div>
          ) : (
            <>
              {/* Header Formulario */}
              <div className="mb-8">
                <h2 className="text-4xl font-bold text-gray-900 mb-3">¿Olvidaste tu contraseña?</h2>
                <p className="text-lg text-gray-600 text-pretty">
                  No te preocupes, te enviaremos instrucciones para restablecerla.
                </p>
              </div>

              {/* Mensaje de Error */}
              {status === "error" && (
                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-start gap-3 text-red-700">
                    <AlertCircle className="w-5 h-5 mt-0.5 flex-shrink-0" />
                    <p className="text-sm font-medium">{errorMessage}</p>
                </div>
              )}

              {/* Form */}
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label htmlFor="email" className="block text-sm font-semibold text-gray-900 mb-2">
                    Correo electrónico
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <Mail className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      placeholder="tu@correo.com"
                      className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition-all"
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
                        <Loader2 className="w-5 h-5 animate-spin" /> Enviando...
                      </>
                  ) : (
                      <>
                        Enviar instrucciones
                        <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
                      </>
                  )}
                </button>
              </form>
            </>
          )}
        </div>
      </div>

      {/* Right side - Visual (Igual que tu código original) */}
      <div className="hidden lg:flex flex-1 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 relative overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0icmdiYSgyNTUsMjU1LDI1NSwwLjAzKSIgc3Ryb2tlLXdpZHRoPSIxIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2dyaWQpIi8+PC9zdmc+')] opacity-40"></div>
        <div className="relative z-10 flex flex-col justify-center px-16 text-white">
          <div className="max-w-lg">
            <div className="w-16 h-16 bg-yellow-500 rounded-2xl flex items-center justify-center mb-8">
              <Mail className="w-8 h-8 text-black" />
            </div>
            <h2 className="text-5xl font-bold mb-6 leading-tight text-balance">
              Recupera tu <span className="text-yellow-500">acceso</span>
            </h2>
            <p className="text-xl text-gray-300 leading-relaxed text-pretty">
              Te enviaremos un enlace seguro a tu correo electrónico para que puedas restablecer tu contraseña de forma rápida y segura.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ForgotPassword