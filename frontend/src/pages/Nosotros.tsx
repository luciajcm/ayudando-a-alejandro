import { Link } from "react-router-dom"
import { Users, Target, Award, Heart, Dumbbell, TrendingUp } from "lucide-react"

function Nosotros() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-black to-gray-900">
      {/* Hero Section */}
      <section className="relative py-20 px-4 overflow-hidden">
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:4rem_4rem]" />
        <div className="max-w-4xl mx-auto text-center relative z-10">
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-6 text-balance">
            Sobre <span className="text-yellow-400">FitHub</span>
          </h1>
          <p className="text-xl text-gray-300 leading-relaxed text-balance">
            Transformando vidas a través del fitness, un entrenamiento a la vez
          </p>
        </div>
      </section>

      {/* Mission Section */}
      <section className="py-16 px-4 bg-gray-900/50">
        <div className="max-w-6xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl font-bold text-white mb-6 flex items-center gap-3">
                <Target className="text-yellow-400" size={36} />
                Nuestra Misión
              </h2>
              <p className="text-gray-300 text-lg leading-relaxed mb-4">
                En FitHub, creemos que el fitness es más que solo ejercicio. Es un estilo de vida, una comunidad y un
                viaje hacia la mejor versión de ti mismo.
              </p>
              <p className="text-gray-300 text-lg leading-relaxed">
                Nuestra plataforma está diseñada para empoderarte con las herramientas, conocimientos y apoyo que
                necesitas para alcanzar tus objetivos de fitness, sin importar dónde comiences.
              </p>
            </div>
            <div className="bg-gradient-to-br from-yellow-400/20 to-yellow-600/20 rounded-2xl p-8 border border-yellow-400/30 backdrop-blur-sm">
              <img
                src="/fitness-team-working-out-together.jpg"
                alt="FitHub Community"
                className="w-full h-auto rounded-lg shadow-2xl"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Values Section */}
      <section className="py-16 px-4">
        <div className="max-w-6xl mx-auto">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Nuestros Valores</h2>
          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-gray-800/50 border border-gray-700 rounded-xl p-8 hover:border-yellow-400/50 transition-all hover:transform hover:scale-105">
              <Heart className="text-yellow-400 mb-4" size={48} />
              <h3 className="text-2xl font-bold text-white mb-3">Pasión</h3>
              <p className="text-gray-300 leading-relaxed">
                Amamos lo que hacemos y nos apasiona ayudar a otros a descubrir su potencial.
              </p>
            </div>

            <div className="bg-gray-800/50 border border-gray-700 rounded-xl p-8 hover:border-yellow-400/50 transition-all hover:transform hover:scale-105">
              <Users className="text-yellow-400 mb-4" size={48} />
              <h3 className="text-2xl font-bold text-white mb-3">Comunidad</h3>
              <p className="text-gray-300 leading-relaxed">
                Creemos en el poder de entrenar juntos y apoyarnos mutuamente en cada paso.
              </p>
            </div>

            <div className="bg-gray-800/50 border border-gray-700 rounded-xl p-8 hover:border-yellow-400/50 transition-all hover:transform hover:scale-105">
              <TrendingUp className="text-yellow-400 mb-4" size={48} />
              <h3 className="text-2xl font-bold text-white mb-3">Progreso</h3>
              <p className="text-gray-300 leading-relaxed">
                Celebramos cada logro, grande o pequeño, porque cada paso cuenta en tu viaje.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 px-4 bg-gray-900/50">
        <div className="max-w-6xl mx-auto">
          <h2 className="text-4xl font-bold text-white text-center mb-12">Nuestro Impacto</h2>
          <div className="grid md:grid-cols-3 gap-8">
            <div className="text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-yellow-400/20 rounded-full mb-4">
                <Users className="text-yellow-400" size={36} />
              </div>
              <div className="text-5xl font-bold text-white mb-2">10K+</div>
              <div className="text-gray-300 text-lg">Miembros Activos</div>
            </div>

            <div className="text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-yellow-400/20 rounded-full mb-4">
                <Dumbbell className="text-yellow-400" size={36} />
              </div>
              <div className="text-5xl font-bold text-white mb-2">500K+</div>
              <div className="text-gray-300 text-lg">Entrenamientos Completados</div>
            </div>

            <div className="text-center">
              <div className="inline-flex items-center justify-center w-20 h-20 bg-yellow-400/20 rounded-full mb-4">
                <Award className="text-yellow-400" size={36} />
              </div>
              <div className="text-5xl font-bold text-white mb-2">95%</div>
              <div className="text-gray-300 text-lg">Satisfacción de Usuarios</div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4">
        <div className="max-w-4xl mx-auto text-center bg-gradient-to-r from-yellow-600 to-yellow-400 rounded-3xl p-12 shadow-2xl">
          <h2 className="text-4xl font-bold text-black mb-6 text-balance">¿Listo para Comenzar tu Transformación?</h2>
          <p className="text-xl text-gray-900 mb-8 text-balance">
            Únete a nuestra comunidad y empieza tu viaje fitness hoy mismo
          </p>
          <Link
            to="/register"
            className="inline-block bg-black text-yellow-400 font-bold text-lg px-10 py-4 rounded-full hover:bg-gray-900 transition-all hover:scale-105 shadow-lg"
          >
            Comenzar Ahora
          </Link>
        </div>
      </section>
    </div>
  )
}

export default Nosotros
