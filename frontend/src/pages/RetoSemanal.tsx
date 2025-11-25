import { Trophy, Calendar, Award, TrendingUp, CheckCircle2, Circle, Lock } from "lucide-react"

function RetoSemanal() {
  const currentDay = 3 // Simula que estamos en el día 3

  const weeklyChallenge = {
    title: "Quema 2,000 Calorías",
    description: "Completa entrenamientos que sumen 2,000 calorías esta semana",
    currentProgress: 1250,
    goal: 2000,
    reward: "500 puntos + Insignia de Fuego",
    daysLeft: 4,
  }

  const dailyChallenges = [
    { day: 1, title: "30 min de Cardio", completed: true, points: 50, icon: "🏃" },
    { day: 2, title: "50 Flexiones", completed: true, points: 30, icon: "💪" },
    { day: 3, title: "5km Corriendo", completed: false, points: 60, icon: "⚡", active: true },
    { day: 4, title: "100 Sentadillas", completed: false, points: 40, icon: "🦵", locked: true },
    { day: 5, title: "Yoga 20 min", completed: false, points: 35, icon: "🧘", locked: true },
    { day: 6, title: "HIIT Intenso", completed: false, points: 70, icon: "🔥", locked: true },
    { day: 7, title: "Desafío Bonus", completed: false, points: 100, icon: "🏆", locked: true },
  ]

  const achievements = [
    { name: "Racha de 7 días", icon: "🔥", unlocked: true, rarity: "Oro" },
    { name: "Madrugador", icon: "🌅", unlocked: true, rarity: "Plata" },
    { name: "Quema Calorías", icon: "💥", unlocked: false, rarity: "Oro", progress: 65 },
    { name: "Constancia Total", icon: "💎", unlocked: false, rarity: "Platino", progress: 40 },
  ]

  const leaderboard = [
    { rank: 1, name: "Ana García", points: 2340, avatar: "👩" },
    { rank: 2, name: "Carlos M.", points: 2180, avatar: "👨" },
    { rank: 3, name: "Tu", points: 2095, avatar: "😊", isUser: true },
    { rank: 4, name: "María L.", points: 1950, avatar: "👩‍🦰" },
    { rank: 5, name: "Diego T.", points: 1820, avatar: "👨‍🎓" },
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold mb-2 bg-gradient-to-r from-yellow-400 to-yellow-500 bg-clip-text text-transparent">
            Reto Semanal
          </h1>
          <p className="text-gray-400">Supera desafíos y gana recompensas increíbles</p>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Reto Principal */}
            <div className="bg-gradient-to-br from-yellow-400/20 to-yellow-500/20 rounded-2xl p-6 border border-yellow-400/30">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-4">
                  <div className="bg-yellow-400/30 p-4 rounded-xl">
                    <Trophy className="w-8 h-8 text-yellow-400" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold mb-1">{weeklyChallenge.title}</h2>
                    <p className="text-gray-300">{weeklyChallenge.description}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-400">Quedan</p>
                  <p className="text-2xl font-bold text-yellow-400">{weeklyChallenge.daysLeft} días</p>
                </div>
              </div>

              {/* Progress Bar */}
              <div className="mb-4">
                <div className="flex justify-between mb-2">
                  <span className="text-sm text-gray-400">Progreso</span>
                  <span className="text-sm font-semibold">
                    {weeklyChallenge.currentProgress} / {weeklyChallenge.goal} kcal
                  </span>
                </div>
                <div className="w-full bg-gray-700 rounded-full h-4">
                  <div
                    className="bg-gradient-to-r from-yellow-400 to-yellow-500 h-4 rounded-full transition-all duration-300 flex items-center justify-end pr-2"
                    style={{ width: `${(weeklyChallenge.currentProgress / weeklyChallenge.goal) * 100}%` }}
                  >
                    <span className="text-xs font-bold text-black">
                      {Math.round((weeklyChallenge.currentProgress / weeklyChallenge.goal) * 100)}%
                    </span>
                  </div>
                </div>
              </div>

              <div className="bg-gray-900/50 rounded-xl p-4 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Award className="w-6 h-6 text-yellow-400" />
                  <div>
                    <p className="text-sm text-gray-400">Recompensa</p>
                    <p className="font-semibold">{weeklyChallenge.reward}</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Desafíos Diarios */}
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-blue-500/20 p-3 rounded-xl">
                  <Calendar className="w-6 h-6 text-blue-400" />
                </div>
                <h3 className="text-xl font-bold">Desafíos Diarios</h3>
              </div>

              <div className="space-y-3">
                {dailyChallenges.map((challenge) => (
                  <div
                    key={challenge.day}
                    className={`p-4 rounded-xl border transition-all ${
                      challenge.completed
                        ? "bg-yellow-400/10 border-yellow-400/30"
                        : challenge.active
                          ? "bg-blue-500/10 border-blue-500/30"
                          : challenge.locked
                            ? "bg-gray-900/30 border-gray-700/30 opacity-60"
                            : "bg-gray-900/50 border-gray-700"
                    } ${!challenge.locked && "hover:scale-105 cursor-pointer"}`}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4">
                        <div className="text-3xl">{challenge.icon}</div>
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="text-xs font-semibold text-gray-400">DÍA {challenge.day}</span>
                            {challenge.active && (
                              <span className="bg-blue-500 text-white text-xs px-2 py-0.5 rounded-full">Hoy</span>
                            )}
                          </div>
                          <h4 className="font-semibold">{challenge.title}</h4>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-right">
                          <p className="text-yellow-400 font-bold">+{challenge.points}</p>
                          <p className="text-xs text-gray-400">puntos</p>
                        </div>
                        {challenge.completed ? (
                          <CheckCircle2 className="w-6 h-6 text-yellow-400" />
                        ) : challenge.locked ? (
                          <Lock className="w-6 h-6 text-gray-600" />
                        ) : (
                          <Circle className="w-6 h-6 text-gray-600" />
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Logros */}
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-2xl p-6 border border-gray-700">
              <div className="flex items-center gap-3 mb-6">
                <div className="bg-purple-500/20 p-3 rounded-xl">
                  <Award className="w-6 h-6 text-purple-400" />
                </div>
                <h3 className="text-xl font-bold">Logros y Medallas</h3>
              </div>

              <div className="grid md:grid-cols-2 gap-4">
                {achievements.map((achievement) => (
                  <div
                    key={achievement.name}
                    className={`p-4 rounded-xl border ${
                      achievement.unlocked
                        ? "bg-gradient-to-br from-yellow-400/10 to-yellow-500/10 border-yellow-400/30"
                        : "bg-gray-900/50 border-gray-700"
                    }`}
                  >
                    <div className="flex items-center gap-3 mb-3">
                      <div className={`text-4xl ${!achievement.unlocked && "grayscale opacity-50"}`}>
                        {achievement.icon}
                      </div>
                      <div className="flex-1">
                        <h4 className="font-semibold">{achievement.name}</h4>
                        <span
                          className={`text-xs px-2 py-0.5 rounded-full ${
                            achievement.rarity === "Platino"
                              ? "bg-cyan-500/20 text-cyan-400"
                              : achievement.rarity === "Oro"
                                ? "bg-yellow-500/20 text-yellow-400"
                                : "bg-gray-500/20 text-gray-400"
                          }`}
                        >
                          {achievement.rarity}
                        </span>
                      </div>
                    </div>
                    {!achievement.unlocked && achievement.progress && (
                      <div>
                        <div className="w-full bg-gray-700 rounded-full h-2">
                          <div
                            className="bg-gradient-to-r from-purple-400 to-pink-400 h-2 rounded-full"
                            style={{ width: `${achievement.progress}%` }}
                          />
                        </div>
                        <p className="text-xs text-gray-400 mt-1">{achievement.progress}% completado</p>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Ranking */}
            <div className="bg-gradient-to-br from-gray-800 to-gray-900 rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4 flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-yellow-400" />
                Ranking Semanal
              </h3>
              <div className="space-y-3">
                {leaderboard.map((user) => (
                  <div
                    key={user.rank}
                    className={`flex items-center gap-3 p-3 rounded-lg ${
                      user.isUser ? "bg-yellow-400/20 border border-yellow-400/30" : "bg-gray-900/50"
                    }`}
                  >
                    <div
                      className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
                        user.rank === 1
                          ? "bg-yellow-500 text-yellow-900"
                          : user.rank === 2
                            ? "bg-gray-400 text-gray-900"
                            : user.rank === 3
                              ? "bg-orange-600 text-orange-100"
                              : "bg-gray-700 text-gray-300"
                      }`}
                    >
                      {user.rank}
                    </div>
                    <div className="text-2xl">{user.avatar}</div>
                    <div className="flex-1">
                      <h4 className="text-sm font-semibold">{user.name}</h4>
                      <p className="text-xs text-gray-400">{user.points} pts</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Stats Card */}
            <div className="bg-gray-800/50 backdrop-blur-sm rounded-xl p-6 border border-gray-700">
              <h3 className="text-lg font-bold mb-4">Tus Estadísticas</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-gray-400 text-sm">Retos Completados</span>
                  <span className="text-xl font-bold text-yellow-400">28</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-400 text-sm">Racha Actual</span>
                  <span className="text-xl font-bold text-orange-400">12 días</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-gray-400 text-sm">Total Puntos</span>
                  <span className="text-xl font-bold text-yellow-400">2,095</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RetoSemanal
