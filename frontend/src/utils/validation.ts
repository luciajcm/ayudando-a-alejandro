export const validatePassword = (password: string): string | null => {
  if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres";
  if (!/[A-Z]/.test(password)) return "Debe incluir al menos una mayúscula";
  if (!/[a-z]/.test(password)) return "Debe incluir al menos una minúscula";
  if (!/\d/.test(password)) return "Debe incluir al menos un número";
  if (!/[@$!%*?&]/.test(password)) return "Debe incluir un carácter especial (@$!%*?&)";
  return null;
};

export const validateEmail = (email: string): string | null => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) return "Ingresa un correo electrónico válido";
  return null;
};

export const validatePhone = (phone: string): string | null => {
  // Regex simple para validar formato internacional opcional y longitud
  const phoneRegex = /^\+?\d{9,15}$/; 
  if (!phoneRegex.test(phone)) return "El teléfono debe tener entre 9 y 15 dígitos";
  return null;
};