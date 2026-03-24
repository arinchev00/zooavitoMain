// utils/fileValidation.js

export const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
export const MAX_TOTAL_SIZE = 50 * 1024 * 1024; // 50 MB
export const MAX_FILES = 10; // Максимальное количество фото

export const validateImageFile = (file) => {
  // Проверка размера
  if (file.size > MAX_FILE_SIZE) {
    return {
      valid: false,
      message: `Файл "${file.name}" превышает максимальный размер ${MAX_FILE_SIZE / 1024 / 1024} МБ`
    };
  }

  // Проверка типа файла
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif'];
  if (!allowedTypes.includes(file.type)) {
    return {
      valid: false,
      message: `Файл "${file.name}" имеет неподдерживаемый формат. Разрешены: JPG, PNG, WEBP, GIF`
    };
  }

  return { valid: true };
};

export const validateAllImages = (files, existingCount = 0) => {
  const errors = [];

  // Проверка количества
  if (existingCount + files.length > MAX_FILES) {
    errors.push(`Максимальное количество фото: ${MAX_FILES}`);
  }

  // Проверка каждого файла
  for (const file of files) {
    const result = validateImageFile(file);
    if (!result.valid) {
      errors.push(result.message);
    }
  }

  // Проверка общего размера
  const totalSize = files.reduce((sum, file) => sum + file.size, 0);
  if (totalSize > MAX_TOTAL_SIZE) {
    errors.push(`Общий размер выбранных файлов превышает ${MAX_TOTAL_SIZE / 1024 / 1024} МБ`);
  }

  return {
    valid: errors.length === 0,
    errors
  };
};