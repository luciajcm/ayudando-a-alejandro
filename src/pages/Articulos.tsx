"use client"

import { useState } from "react"
import { BookOpen, Clock, TrendingUp, Heart, Share2, Search, ChevronRight } from "lucide-react"

function Articulos() {
  const [selectedCategory, setSelectedCategory] = useState("all")

  const categories = ["all", "Entrenamiento", "Nutrición", "Recuperación", "Motivación", "Bienestar"]

  const articles = [
    {
      id: 1,
      title: "10 Ejercicios Para Fortalecer tu Core en Casa",
      excerpt: "Descubre los mejores ejercicios para desarrollar un core fuerte sin necesidad de equipamiento...",
      category: "Entrenamiento",
      readTime: 8,
      image: "💪",
      color: "from-blue-500 to-cyan-500",
      likes: 234,
      featured: true,
    },
    {
      id: 2,
      title: "Guía Completa de Nutrición Pre y Post Entrenamiento",
      excerpt: "Aprende qué comer antes y después de tus sesiones para maximizar resultados...",
      category: "Nutrición",
      readTime: 12,
      image: "🥗",
      color: "from-green-500 to-emerald-500",
      likes: 189,
      featured: true,
    },
    {
      id: 3,
      title: "La Importancia del Descanso en tu Progreso Fitness",
      excerpt: "Por qué dormir bien es tan importante como entrenar duro para lograr tus metas...",
      category: "Recuperación",
      readTime: 6,
      image: "😴",
      color: "from-purple-500 to-pink-500",
      likes: 156,
    },
    {
      id: 4,
      title: "Cómo Mantener la Motivación en tu Viaje Fitness",
      excerpt: "Estrategias probadas para mantenerte enfocado y comprometido con tus objetivos...",
      category: "Motivación",
      readTime: 7,
      image: "🔥",
      color: "from-orange-500 to-red-500",
      likes: 298,
    },
    {
      id: 5,
      title: "Rutina de Movilidad para Prevenir Lesiones",
      excerpt: "Ejercicios esenciales de movilidad que deberías incluir en tu rutina diaria...",
      category: "Bienestar",
      readTime: 10,
      image: "🧘",
      color: "from-indigo-500 to-purple-500",
      likes: 167,
    },
    {
      id: 6,
      title: "Proteínas: ¿Cuánto Necesitas Realmente?",
      excerpt: "Desmitificando el consumo de proteínas según tus objetivos de fitness...",
      category: "Nutrición",
      readTime: 9,
      image: "🍗",
      color: "from-red-500 to-pink-500",
      likes: 201,
    },
  ]

  const tips = [
    { icon: "💧", title: "Hidrátate", tip: "Bebe al menos 2-3 litros de agua al día" },
    { icon: "⏰", title: "Consistencia", tip: "El éxito viene de la constancia, no de la intensidad" },
    { icon: "🍎", title: "Come Real", tip: "Prioriza alimentos enteros y no procesados" },
    { icon: "😴", title: "Descansa", tip: "7-9 horas de sueño son cruciales para recuperación" },
  ]

  const filteredArticles =
    selectedCategory === "all" ? articles : articles.filter((a) => a.category === selectedCategory)

  const featuredArticles = articles.filter((a) => a.featured)

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
            Artículos y Consejos
          </h1>
          <p className="text-gray-400">Conocimiento experto para potenciar tu entrenamiento</p>
        </div>

        {/* Search Bar */}
        <div className="mb-6">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Buscar artículos..."
              className="w-full bg-gray-800/50 border border-gray-700 rounded-xl pl-12 pr-4 py-3 focus:border-yellow-400 focus:outline-none transition-colors"
            />
          </div>
        </div>

        {/* Categories Filter */}
        <div className="flex gap-3 mb-8 overflow-x-auto pb-2">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-2 rounded-lg whitespace-nowrap transition-all ${
                selectedCategory === cat
                  ? "bg-yellow-400 text-black font-semibold"
                  : "bg-gray-800 text-gray-400 hover:bg-gray-700"
              }`}
            >
              {cat === "all" ? "Todos" : cat}
            </button>
          ))}
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Featured Articles */}
            {selectedCategory === "all" && (
              <div className="mb-8">
                <h2 className="text-2xl font-bold mb-4 flex items-center gap-2">
                  <TrendingUp className="w-6 h-6 text-yellow-400" />
                  Artículos Destacados
                </h2>
                <div className="grid md:grid-cols-2 gap-4">
                  {featuredArticles.map((article) => (
                    <div
                      key={article.id}
                      className="bg-gray-800/50 backdrop-blur-sm rounded-xl overflow-hidden border border-gray-700 hover:border-yellow-400/50 transition-all hover:scale-105 cursor-pointer group"
                    >
                      <div
                        className={`bg-gradient-to-br ${article.color} h-32 flex items-center justify-center text-5xl`}
                      >
                        {article.image}
                      </div>
                      <div className="p-5">
                        <div className="flex items-center justify-between mb-3">
                          <span className="bg-gray-700 text-gray-300 text-xs px-2 py-1 rounded-full">
                            {article.category}
                          </span>
                          <div className="flex items-center gap-1 text-gray-400 text-sm">
                            <Clock className="w-4 h-4" />
                            <span>{article.readTime} min</span>
                          </div>
                        </div>
                        <h3 className="text-lg font-bold mb-2 group-hover:text-yellow-400 transition-colors leading-tight">
                          {article.title}
                        </h3>
                        <p className="text-sm text-gray-400 line-clamp-2">{article.excerpt}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* All Articles */}
            <div className="space-y-4">
              {filteredArticles.map((article) => (
                <div
                  key={article.id}
                  className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-5 border border-gray-700 hover:border-gray-600 transition-all cursor-pointer group"
                >
                  <div className="flex gap-4">
                    <div
                      className={`bg-gradient-to-br ${article.color} w-24 h-24 rounded-lg flex items-center justify-center text-4xl flex-shrink-0`}
                    >
                      {article.image}
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center gap-3 mb-2">
                        <span className="bg-gray-700 text-gray-300 text-xs px-2 py-1 rounded-full">
                          {article.category}
                        </span>
                        <div className="flex items-center gap-1 text-gray-400 text-sm">
                          <Clock className="w-4 h-4" />
                          <span>{article.readTime} min lectura</span>
                        </div>
                      </div>
                      <h3 className="text-xl font-bold mb-2 group-hover:text-yellow-400 transition-colors">
                        {article.title}
                      </h3>
                      <p className="text-gray-400 text-sm mb-3">{article.excerpt}</p>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4 text-gray-400 text-sm">
                          <button className="flex items-center gap-1 hover:text-red-400 transition-colors">
                            <Heart className="w-4 h-4" />
                            <span>{article.likes}</span>
                          </button>
                          <button className="flex items-center gap-1 hover:text-blue-400 transition-colors">
                            <Share2 className="w-4 h-4" />
                            <span>Compartir</span>
                          </button>
                        </div>
                        <button className="text-yellow-400 font-semibold flex items-center gap-1 hover:gap-2 transition-all">
                          Leer más
                          <ChevronRight className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Tips Rápidos */}
            <div className="bg-gradient-to-br from-gray-800 to-gray-900 rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <BookOpen className="w-5 h-5 text-yellow-400" />
                Tips Rápidos
              </h3>
              <div className="space-y-4">
                {tips.map((tip, index) => (
                  <div key={index} className="bg-gray-900/50 rounded-lg p-4">
                    <div className="flex items-start gap-3">
                      <span className="text-2xl">{tip.icon}</span>
                      <div>
                        <h4 className="font-semibold text-sm mb-1">{tip.title}</h4>
                        <p className="text-xs text-gray-400">{tip.tip}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Categorías Populares */}
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4">Categorías Populares</h3>
              <div className="space-y-2">
                {categories.slice(1).map((cat) => {
                  const count = articles.filter((a) => a.category === cat).length
                  return (
                    <button
                      key={cat}
                      onClick={() => setSelectedCategory(cat)}
                      className="w-full flex items-center justify-between p-3 bg-gray-900/50 rounded-lg hover:bg-gray-900/80 transition-colors"
                    >
                      <span className="text-sm">{cat}</span>
                      <span className="text-xs bg-gray-700 px-2 py-1 rounded-full">{count}</span>
                    </button>
                  )
                })}
              </div>
            </div>

            {/* Newsletter */}
            <div className="bg-gradient-to-br from-yellow-400/20 to-yellow-500/20 rounded-xl p-6 border border-yellow-400/30">
              <h3 className="text-lg font-bold mb-2">Newsletter FitHub</h3>
              <p className="text-sm text-gray-300 mb-4">Recibe los mejores artículos en tu correo</p>
              <input
                type="email"
                placeholder="tu@email.com"
                className="w-full bg-gray-900/50 border border-gray-700 rounded-lg px-4 py-2 mb-3 focus:border-yellow-400 focus:outline-none transition-colors"
              />
              <button className="w-full bg-yellow-400 hover:bg-yellow-500 text-black py-2 rounded-lg font-semibold transition-colors">
                Suscribirse
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Articulos
