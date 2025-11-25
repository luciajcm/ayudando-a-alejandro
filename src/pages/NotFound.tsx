"use client"

import { Link } from "react-router-dom"
import { Home, Search, ArrowLeft, Dumbbell } from "lucide-react"

const NotFound = () => {
  return (
    <div className="min-h-screen bg-black flex items-center justify-center px-6">
      <div className="max-w-2xl w-full text-center">
        {/* Animated 404 */}
        <div className="relative mb-12">
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-64 h-64 bg-yellow-500/10 rounded-full blur-3xl animate-pulse"></div>
          </div>
          <h1 className="relative text-[150px] md:text-[200px] font-black text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-yellow-600 leading-none">
            404
          </h1>
        </div>

        {/* Icon */}
        <div className="flex justify-center mb-6">
          <div className="w-20 h-20 bg-yellow-500/10 rounded-2xl flex items-center justify-center border border-yellow-500/20">
            <Dumbbell className="w-10 h-10 text-yellow-500" />
          </div>
        </div>

        {/* Message */}
        <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">Página no encontrada</h2>
        <p className="text-lg text-gray-400 mb-8 max-w-md mx-auto leading-relaxed">
          Lo sentimos, la página que buscas no existe o ha sido movida. Pero no te preocupes, tenemos mucho más que
          ofrecer.
        </p>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center items-center mb-12">
          <Link
            to="/"
            className="group inline-flex items-center gap-3 bg-yellow-500 hover:bg-yellow-400 text-black px-8 py-4 rounded-xl font-semibold transition-all duration-300 hover:shadow-xl hover:shadow-yellow-500/20 hover:scale-105"
          >
            <Home className="w-5 h-5 group-hover:scale-110 transition-transform" />
            Ir al inicio
          </Link>

          <button
            onClick={() => window.history.back()}
            className="group inline-flex items-center gap-3 bg-zinc-900 hover:bg-zinc-800 text-white px-8 py-4 rounded-xl font-semibold border border-zinc-800 transition-all duration-300 hover:shadow-xl hover:scale-105"
          >
            <ArrowLeft className="w-5 h-5 group-hover:-translate-x-1 transition-transform" />
            Volver atrás
          </button>
        </div>

        {/* Quick Links */}
        <div className="border-t border-zinc-900 pt-8">
          <p className="text-sm text-gray-500 mb-4">Enlaces rápidos</p>
          <div className="flex flex-wrap gap-4 justify-center">
            <Link
              to="/nosotros"
              className="text-sm text-gray-400 hover:text-yellow-500 transition-colors duration-200 hover:underline"
            >
              Sobre Nosotros
            </Link>
            <Link
              to="/dashboard"
              className="text-sm text-gray-400 hover:text-yellow-500 transition-colors duration-200 hover:underline"
            >
              Dashboard
            </Link>
            <Link
              to="/dashboard/entrenamiento"
              className="text-sm text-gray-400 hover:text-yellow-500 transition-colors duration-200 hover:underline"
            >
              Entrenamientos
            </Link>
            <Link
              to="/dashboard/comunidad"
              className="text-sm text-gray-400 hover:text-yellow-500 transition-colors duration-200 hover:underline"
            >
              Comunidad
            </Link>
          </div>
        </div>

        {/* Help Text */}
        <div className="mt-12 p-6 bg-zinc-900 border border-zinc-800 rounded-2xl backdrop-blur-sm">
          <div className="flex items-start gap-3 text-left">
            <Search className="w-5 h-5 text-yellow-500 mt-1 flex-shrink-0" />
            <div>
              <h3 className="text-white font-semibold mb-1">¿Necesitas ayuda?</h3>
              <p className="text-sm text-gray-400">
                Si crees que esto es un error o necesitas asistencia, puedes contactarnos o usar el buscador en la
                página principal.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default NotFound
