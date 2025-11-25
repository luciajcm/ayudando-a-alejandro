import { Link } from "react-router-dom"

function Navbar() {
  return (
    <nav className="bg-black text-white border-b border-yellow-500/20 sticky top-0 z-50 backdrop-blur-sm bg-black/95">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-3 group">
            <img
              src="/images/FitHub_Logo.png"
              alt="FitHub Logo"
              className="h-10 w-auto group-hover:scale-105 transition-transform"
            />
            <span className="text-2xl font-bold bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
              FitHub
            </span>
          </Link>

          {/* Navigation */}
          <div className="flex items-center space-x-8">
            <Link
              to="/nosotros"
              className="text-gray-300 hover:text-yellow-500 font-medium transition-colors relative group"
            >
              Nosotros
              <span className="absolute bottom-0 left-0 w-0 h-0.5 bg-yellow-500 group-hover:w-full transition-all duration-300"></span>
            </Link>

            <div className="flex items-center space-x-3">
              <Link to="/login" className="px-4 py-2 text-white hover:text-yellow-500 font-medium transition-colors">
                Iniciar Sesión
              </Link>
              <Link
                to="/register"
                className="px-6 py-2 bg-yellow-500 text-black font-semibold rounded-lg hover:bg-yellow-400 transition-all hover:shadow-lg hover:shadow-yellow-500/50 hover:-translate-y-0.5"
              >
                Registrarse
              </Link>
              
            </div>
          </div>
          
        </div>
      </div>
    </nav>
  )
}

export default Navbar
