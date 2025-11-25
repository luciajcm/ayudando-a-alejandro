import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

// URL Base (Ajusta si tu backend corre en otro puerto)
const BASE_URL = 'http://localhost:8080/api';

// Tipos
interface AuthResponse {
  message: string;
  accessToken: string;
  refreshToken: string;
  email?: string;
}

// Interfaz para extender la configuración de Axios y evitar bucles infinitos
interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

// 1. Instancia Principal
export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 2. Interceptor de Request (Inyectar Token)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 3. Interceptor de Response (Manejo de Refresh Token)
api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as CustomAxiosRequestConfig;

    // Si el error es 401 (No autorizado) y no es un reintento...
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // Marcamos para no volver a intentar infinitamente

      try {
        const currentRefreshToken = localStorage.getItem('refreshToken');
        
        if (!currentRefreshToken) {
            throw new Error("No hay refresh token");
        }

        // Llamamos al endpoint de refresh del backend
        // Nota: Usamos axios.post directo para no usar los interceptores de 'api'
        const response = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken: currentRefreshToken
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data;

        // Guardamos los nuevos tokens
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        // Actualizamos el header de la petición original
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;

        // Reintentamos la petición original con el nuevo token
        return api(originalRequest);

      } catch (refreshError) {
        // Si falla el refresh (token expirado o inválido), cerramos sesión
        console.error("Sesión expirada. Redirigiendo al login...");
        localStorage.clear(); // Borrar todo
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

// --- Funciones de Auth ---

// Login con Google
export const googleSignIn = async (googleToken: string): Promise<AuthResponse> => {
    // El backend espera { "token": "..." } según tu record GoogleSignInRequest
    const response = await api.post('/auth/google', { token: googleToken });
    return response.data;
};

// Login Normal
export const signIn = async (data: any): Promise<AuthResponse> => {
    const response = await api.post('/auth/signin', data);
    return response.data;
};

// Registro Normal
export const signUp = async (data: any): Promise<AuthResponse> => {
    const response = await api.post('/auth/signup', data);
    return response.data;
};

// Obtener perfil del usuario logueado
export const getProfile = async () => {
  // Asumo que tienes un endpoint para ver el propio perfil. 
  // Si no, usa el que busca por email o username.
  // Lo ideal en Spring Boot es un endpoint: GET /api/users/me
  const response = await api.get('/users/me'); 
  return response.data;
};

// Actualizar perfil (Para el Onboarding de Google)
export const updateProfile = async (userId: number, data: any) => {
  const response = await api.put(`/users/${userId}`, data);
  return response.data;
};

// ... al final del archivo
export const requestPasswordReset = async (email: string) => {
    const response = await api.post('/auth/forgot-password', { email });
    return response.data;
};

// Resetear la contraseña usando el token
export const resetPassword = async (token: string, newPassword: string) => {
    const response = await api.post('/auth/reset-password', { token, newPassword });
    return response.data;
};

// --- EJERCICIOS ---
export const getExercises = async () => {
    const response = await api.get('/exercises'); 
    return response.data;
};

// --- RUTINAS ---
// 1. Crear la rutina base (Nombre y Día)
export const createRoutine = async (data: { name: string; day: string }) => {
    const response = await api.post('/routines', data);
    return response.data;
};

// 2. Agregar ejercicio a la rutina (Uno por uno, según tu backend)
export const addExerciseToRoutine = async (routineId: number, exerciseData: any) => {
    // exerciseData debe coincidir con RoutineExerciseDto del backend
    const response = await api.post(`/routines/${routineId}/exercise`, exerciseData);
    return response.data;
};

export const getRoutines = async (page = 0, size = 100) => {
    const response = await api.get(`/routines?page=${page}&size=${size}`);
    return response.data;
};

// --- PROGRAMAS ---
export const createProgram = async (data: any) => {
    const response = await api.post('/programs', data);
    return response.data;
};

export const getPrograms = async (learnerId: number) => {
    const response = await api.get(`/programs/${learnerId}`);
    return response.data;
};