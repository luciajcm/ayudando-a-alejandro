"use client"

import type React from "react"
import { useState } from "react"
import { Link } from "react-router-dom"
import { Users, Dumbbell, TrendingUp, Zap, ChevronLeft, ChevronRight } from "lucide-react"

const images = [
  "https://i.blogs.es/119852/1366_2000/1366_2000.jpeg",
  "https://images.squarespace-cdn.com/content/v1/659d79bfcbafbe5e3269d464/bf5ee21d-876b-4307-9e55-2f615353238c/rutina-gimnasio-principiantes.jpg",
  "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTsuG90IDKpSVrZ_P9k_dZv8GuUl8TiC6Tnuw&s",
]

const Home: React.FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0)

  const goToPrevious = () => {
    const isFirstSlide = currentIndex === 0
    const newIndex = isFirstSlide ? images.length - 1 : currentIndex - 1
    setCurrentIndex(newIndex)
  }

  const goToNext = () => {
    const isLastSlide = currentIndex === images.length - 1
    const newIndex = isLastSlide ? 0 : currentIndex + 1
    setCurrentIndex(newIndex)
  }

  const goToSlide = (slideIndex: number) => {
    setCurrentIndex(slideIndex)
  }

  return (
    <div className="bg-black">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-br from-black via-gray-900 to-black text-white overflow-hidden">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZGVmcz48cGF0dGVybiBpZD0iZ3JpZCIgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSJyZ2JhKDIzNCwxNzksOCwwLjA1KSIgc3Ryb2tlLXdpZHRoPSIxIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2dyaWQpIi8+PC9zdmc+')] opacity-30"></div>

        <div className="container mx-auto px-6 relative z-10">
          <div className="py-20 md:py-32 text-center max-w-5xl mx-auto">
            <div className="inline-flex items-center gap-2 bg-yellow-500/10 border border-yellow-500/30 rounded-full px-4 py-2 mb-8 animate-fade-in-up">
              <Zap className="w-4 h-4 text-yellow-500" />
              <span className="text-sm font-medium text-yellow-400">Tu viaje fitness comienza aquí</span>
            </div>

            <h1 className="text-5xl md:text-7xl font-bold mb-6 leading-tight text-balance">
              Conecta. Entrena.{" "}
              <span className="bg-gradient-to-r from-yellow-500 to-yellow-300 bg-clip-text text-transparent">
                Transforma.
              </span>
            </h1>

            <p className="text-xl md:text-2xl text-gray-300 max-w-3xl mx-auto mb-10 leading-relaxed text-pretty">
              La plataforma definitiva para encontrar entrenadores expertos, gestionar tus rutinas y alcanzar tus metas
              fitness.
            </p>

            <div className="flex flex-col sm:flex-row justify-center gap-4 mb-12">
              <Link
                to="/register"
                className="group bg-yellow-500 text-black font-bold py-4 px-8 rounded-xl text-lg hover:bg-yellow-400 transition-all duration-300 hover:scale-105 hover:shadow-lg hover:shadow-yellow-500/50"
              >
                Empezar Ahora
                <span className="inline-block transition-transform group-hover:translate-x-1 ml-2">→</span>
              </Link>
              <Link
                to="/nosotros"
                className="bg-white/10 backdrop-blur-sm text-white font-semibold py-4 px-8 rounded-xl text-lg hover:bg-white/20 transition-all duration-300 border border-white/20 hover:border-yellow-500/50"
              >
                Conoce Más
              </Link>
            </div>

            <div className="grid grid-cols-3 gap-8 max-w-2xl mx-auto pt-8 border-t border-yellow-500/20">
              <div>
                <div className="text-3xl font-bold text-yellow-500">1000+</div>
                <div className="text-sm text-gray-400 mt-1">Entrenadores</div>
              </div>
              <div>
                <div className="text-3xl font-bold text-yellow-500">50K+</div>
                <div className="text-sm text-gray-400 mt-1">Usuarios Activos</div>
              </div>
              <div>
                <div className="text-3xl font-bold text-yellow-500">4.9★</div>
                <div className="text-sm text-gray-400 mt-1">Valoración</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Features Section */}
      <div className="container mx-auto px-6 py-20 md:py-28">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold text-white mb-4 text-balance">
            Todo lo que necesitas para{" "}
            <span className="bg-gradient-to-r from-yellow-500 to-yellow-300 bg-clip-text text-transparent">
              triunfar
            </span>
          </h2>
          <p className="text-xl text-gray-400 max-w-2xl mx-auto text-pretty">
            Herramientas profesionales diseñadas para tu progreso
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
          <div className="group bg-gray-900 p-8 rounded-2xl border border-gray-800 hover:border-yellow-500/50 hover:shadow-xl hover:shadow-yellow-500/10 transition-all duration-300">
            <div className="w-14 h-14 bg-yellow-500/10 rounded-xl flex items-center justify-center mb-6 group-hover:bg-yellow-500 group-hover:scale-110 transition-all duration-300">
              <Users className="w-7 h-7 text-yellow-500 group-hover:text-black transition-colors duration-300" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-3">Encuentra tu Entrenador</h3>
            <p className="text-gray-400 leading-relaxed">
              Conecta con profesionales certificados que se adapten a tu estilo y objetivos personales.
            </p>
          </div>

          <div className="group bg-gray-900 p-8 rounded-2xl border border-gray-800 hover:border-yellow-500/50 hover:shadow-xl hover:shadow-yellow-500/10 transition-all duration-300">
            <div className="w-14 h-14 bg-yellow-500/10 rounded-xl flex items-center justify-center mb-6 group-hover:bg-yellow-500 group-hover:scale-110 transition-all duration-300">
              <Dumbbell className="w-7 h-7 text-yellow-500 group-hover:text-black transition-colors duration-300" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-3">Gestiona tus Rutinas</h3>
            <p className="text-gray-400 leading-relaxed">
              Accede a planes personalizados, registra tu progreso y mantente motivado cada día.
            </p>
          </div>

          <div className="group bg-gray-900 p-8 rounded-2xl border border-gray-800 hover:border-yellow-500/50 hover:shadow-xl hover:shadow-yellow-500/10 transition-all duration-300">
            <div className="w-14 h-14 bg-yellow-500/10 rounded-xl flex items-center justify-center mb-6 group-hover:bg-yellow-500 group-hover:scale-110 transition-all duration-300">
              <TrendingUp className="w-7 h-7 text-yellow-500 group-hover:text-black transition-colors duration-300" />
            </div>
            <h3 className="text-2xl font-bold text-white mb-3">Sigue tu Progreso</h3>
            <p className="text-gray-400 leading-relaxed">
              Visualiza tu evolución con métricas detalladas y celebra cada logro alcanzado.
            </p>
          </div>
        </div>
      </div>

      {/* Carousel Section */}
      <div className="bg-gray-900 py-20">
        <div className="container mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">Inspiración Fitness</h2>
            <p className="text-xl text-gray-400">Tu transformación empieza hoy</p>
          </div>

          <div className="relative max-w-5xl mx-auto rounded-3xl overflow-hidden shadow-2xl shadow-black/50">
            <div
              className="flex transition-transform duration-500 ease-out"
              style={{ transform: `translateX(-${currentIndex * 100}%)` }}
            >
              {images.map((src, index) => (
                <div key={index} className="w-full flex-shrink-0">
                  <img
                    src={src || "/placeholder.svg"}
                    alt={`Inspiración fitness ${index + 1}`}
                    className="w-full h-[500px] object-cover"
                  />
                </div>
              ))}
            </div>

            <button
              onClick={goToPrevious}
              className="absolute top-1/2 left-6 -translate-y-1/2 w-12 h-12 bg-yellow-500 text-black rounded-full flex items-center justify-center hover:bg-yellow-400 hover:scale-110 transition-all duration-300 shadow-lg"
              aria-label="Anterior"
            >
              <ChevronLeft className="w-6 h-6" />
            </button>
            <button
              onClick={goToNext}
              className="absolute top-1/2 right-6 -translate-y-1/2 w-12 h-12 bg-yellow-500 text-black rounded-full flex items-center justify-center hover:bg-yellow-400 hover:scale-110 transition-all duration-300 shadow-lg"
              aria-label="Siguiente"
            >
              <ChevronRight className="w-6 h-6" />
            </button>

            <div className="absolute bottom-6 left-0 right-0 flex justify-center gap-2">
              {images.map((_, index) => (
                <button
                  key={index}
                  onClick={() => goToSlide(index)}
                  className={`h-2 rounded-full transition-all duration-300 ${
                    currentIndex === index ? "w-8 bg-yellow-500" : "w-2 bg-white/50"
                  }`}
                  aria-label={`Ir a imagen ${index + 1}`}
                />
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* CTA Section */}
      <div className="bg-gradient-to-r from-yellow-500 to-yellow-400 py-20">
        <div className="container mx-auto px-6 text-center">
          <Zap className="w-16 h-16 text-black mx-auto mb-6 animate-pulse" />
          <h2 className="text-4xl md:text-5xl font-bold text-black mb-6 text-balance">
            Únete a miles que ya están transformando sus vidas
          </h2>
          <p className="text-xl text-black/80 max-w-2xl mx-auto mb-10 text-pretty">
            Comienza tu viaje fitness hoy y descubre tu mejor versión
          </p>
          <Link
            to="/register"
            className="inline-block bg-black text-yellow-500 font-bold py-4 px-10 rounded-xl text-lg hover:bg-gray-900 transition-all duration-300 hover:scale-105 hover:shadow-2xl"
          >
            Crear Cuenta Gratis
          </Link>
        </div>
      </div>
    </div>
    // </CHANGE>
  )
}

export default Home
