"use client"

import { useState } from "react"
import { Users, MessageCircle, Heart, Share2, TrendingUp, Award } from "lucide-react"

function Comunidad() {
  const [selectedTab, setSelectedTab] = useState("feed")

  const posts = [
    {
      id: 1,
      user: "Ana García",
      avatar: "👩",
      time: "Hace 2 horas",
      content: "¡Completé mi primer maratón! 42km en 4:15:30. No puedo creer que lo logré 🏃‍♀️💪",
      image: "🏃‍♀️",
      likes: 124,
      comments: 18,
      achievement: "Primer Maratón",
    },
    {
      id: 2,
      user: "Carlos Mendoza",
      avatar: "👨",
      time: "Hace 5 horas",
      content: "Nueva PR en deadlift: 180kg! El entrenamiento de fuerza está dando resultados 💪",
      image: "🏋️",
      likes: 89,
      comments: 12,
    },
    {
      id: 3,
      user: "María López",
      avatar: "👩‍🦰",
      time: "Hace 1 día",
      content: "30 días consecutivos entrenando. La constancia es clave! Quién se une al reto? 🔥",
      image: "🔥",
      likes: 156,
      comments: 24,
      achievement: "Racha 30 días",
    },
  ]

  const topUsers = [
    { name: "Pedro Ruiz", avatar: "👨‍💼", workouts: 156, points: 2340 },
    { name: "Laura Sánchez", avatar: "👩‍💻", workouts: 142, points: 2180 },
    { name: "Diego Torres", avatar: "👨‍🎓", workouts: 138, points: 2095 },
  ]

  const groups = [
    { name: "Runners Matutinos", members: 1240, icon: "🏃", color: "from-blue-500 to-cyan-500" },
    { name: "Fuerza y Músculo", members: 890, icon: "💪", color: "from-red-500 to-orange-500" },
    { name: "Yoga y Bienestar", members: 756, icon: "🧘", color: "from-purple-500 to-pink-500" },
    { name: "Nutrición Saludable", members: 1100, icon: "🥗", color: "from-green-500 to-emerald-500" },
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
            Comunidad FitHub
          </h1>
          <p className="text-gray-400">Conecta, comparte y motiva a otros</p>
        </div>

        {/* Tabs */}
        <div className="flex gap-4 mb-6 border-b border-gray-700 pb-4">
          <button
            onClick={() => setSelectedTab("feed")}
            className={`px-4 py-2 rounded-lg transition-all ${
              selectedTab === "feed"
                ? "bg-yellow-400 text-black font-semibold"
                : "bg-gray-800 text-gray-400 hover:bg-gray-700"
            }`}
          >
            Feed
          </button>
          <button
            onClick={() => setSelectedTab("groups")}
            className={`px-4 py-2 rounded-lg transition-all ${
              selectedTab === "groups"
                ? "bg-yellow-400 text-black font-semibold"
                : "bg-gray-800 text-gray-400 hover:bg-gray-700"
            }`}
          >
            Grupos
          </button>
          <button
            onClick={() => setSelectedTab("leaderboard")}
            className={`px-4 py-2 rounded-lg transition-all ${
              selectedTab === "leaderboard"
                ? "bg-yellow-400 text-black font-semibold"
                : "bg-gray-800 text-gray-400 hover:bg-gray-700"
            }`}
          >
            Ranking
          </button>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {selectedTab === "feed" && (
              <>
                {/* Crear Post */}
                <div className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-4 border border-gray-700">
                  <div className="flex gap-4">
                    <div className="text-3xl">👤</div>
                    <input
                      type="text"
                      placeholder="Comparte tu progreso..."
                      className="flex-1 bg-gray-900/50 rounded-lg px-4 py-2 border border-gray-700 focus:border-yellow-400 focus:outline-none transition-colors"
                    />
                    <button className="bg-yellow-400 hover:bg-yellow-500 text-black px-6 py-2 rounded-lg font-semibold transition-colors">
                      Publicar
                    </button>
                  </div>
                </div>

                {/* Posts */}
                {posts.map((post) => (
                  <div
                    key={post.id}
                    className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700 hover:border-gray-600 transition-all"
                  >
                    <div className="flex items-start gap-4 mb-4">
                      <div className="text-3xl">{post.avatar}</div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <div>
                            <h4 className="font-bold">{post.user}</h4>
                            <p className="text-sm text-gray-400">{post.time}</p>
                          </div>
                          {post.achievement && (
                            <div className="bg-yellow-400/20 px-3 py-1 rounded-full flex items-center gap-2">
                              <Award className="w-4 h-4 text-yellow-400" />
                              <span className="text-xs text-yellow-400 font-semibold">{post.achievement}</span>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>

                    <p className="text-gray-300 mb-4">{post.content}</p>

                    <div className="bg-gradient-to-br from-gray-700 to-gray-800 h-48 rounded-lg flex items-center justify-center text-6xl mb-4">
                      {post.image}
                    </div>

                    <div className="flex items-center gap-6 pt-4 border-t border-gray-700">
                      <button className="flex items-center gap-2 text-gray-400 hover:text-red-400 transition-colors">
                        <Heart className="w-5 h-5" />
                        <span>{post.likes}</span>
                      </button>
                      <button className="flex items-center gap-2 text-gray-400 hover:text-blue-400 transition-colors">
                        <MessageCircle className="w-5 h-5" />
                        <span>{post.comments}</span>
                      </button>
                      <button className="flex items-center gap-2 text-gray-400 hover:text-yellow-400 transition-colors">
                        <Share2 className="w-5 h-5" />
                        <span>Compartir</span>
                      </button>
                    </div>
                  </div>
                ))}
              </>
            )}

            {selectedTab === "groups" && (
              <div className="grid md:grid-cols-2 gap-4">
                {groups.map((group) => (
                  <div
                    key={group.name}
                    className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700 hover:border-yellow-400/50 transition-all cursor-pointer group"
                  >
                    <div
                      className={`bg-gradient-to-br ${group.color} w-16 h-16 rounded-xl flex items-center justify-center text-3xl mb-4`}
                    >
                      {group.icon}
                    </div>
                    <h3 className="text-xl font-bold mb-2 group-hover:text-yellow-400 transition-colors">
                      {group.name}
                    </h3>
                    <p className="text-gray-400 flex items-center gap-2">
                      <Users className="w-4 h-4" />
                      {group.members.toLocaleString()} miembros
                    </p>
                    <button className="mt-4 w-full bg-yellow-400/20 hover:bg-yellow-400 text-yellow-400 hover:text-black py-2 rounded-lg transition-all font-semibold">
                      Unirse
                    </button>
                  </div>
                ))}
              </div>
            )}

            {selectedTab === "leaderboard" && (
              <div className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700">
                <h3 className="text-xl font-bold mb-6">Top Usuarios del Mes</h3>
                <div className="space-y-4">
                  {topUsers.map((user, index) => (
                    <div
                      key={user.name}
                      className="flex items-center justify-between p-4 bg-gray-900/50 rounded-xl hover:bg-gray-900/80 transition-colors"
                    >
                      <div className="flex items-center gap-4">
                        <div
                          className={`w-10 h-10 rounded-full flex items-center justify-center font-bold ${
                            index === 0
                              ? "bg-yellow-500 text-yellow-900"
                              : index === 1
                                ? "bg-gray-400 text-gray-900"
                                : "bg-orange-600 text-orange-100"
                          }`}
                        >
                          {index + 1}
                        </div>
                        <div className="text-3xl">{user.avatar}</div>
                        <div>
                          <h4 className="font-semibold">{user.name}</h4>
                          <p className="text-sm text-gray-400">{user.workouts} entrenamientos</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-2xl font-bold text-yellow-400">{user.points}</p>
                        <p className="text-xs text-gray-400">puntos</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Estadísticas Comunidad */}
            <div className="bg-gradient-to-br from-gray-800 to-gray-900 rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-yellow-400" />
                Estadísticas
              </h3>
              <div className="space-y-4">
                <div>
                  <p className="text-gray-400 text-sm">Usuarios Activos</p>
                  <p className="text-2xl font-bold text-yellow-400">3,986</p>
                </div>
                <div>
                  <p className="text-gray-400 text-sm">Entrenamientos Hoy</p>
                  <p className="text-2xl font-bold text-blue-400">1,254</p>
                </div>
                <div>
                  <p className="text-gray-400 text-sm">Posts esta Semana</p>
                  <p className="text-2xl font-bold text-purple-400">892</p>
                </div>
              </div>
            </div>

            {/* Grupos Sugeridos */}
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4">Grupos Sugeridos</h3>
              <div className="space-y-3">
                <div className="flex items-center gap-3 p-2 hover:bg-gray-900/50 rounded-lg cursor-pointer transition-colors">
                  <div className="text-2xl">🚴</div>
                  <div className="flex-1">
                    <h4 className="text-sm font-semibold">Ciclismo Urbano</h4>
                    <p className="text-xs text-gray-400">654 miembros</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-2 hover:bg-gray-900/50 rounded-lg cursor-pointer transition-colors">
                  <div className="text-2xl">🏊</div>
                  <div className="flex-1">
                    <h4 className="text-sm font-semibold">Natación Pro</h4>
                    <p className="text-xs text-gray-400">432 miembros</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Comunidad
