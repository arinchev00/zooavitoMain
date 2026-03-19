# frontend/Dockerfile

# ---- Этап сборки ----
FROM node:20-alpine AS build

WORKDIR /app

# Копируем файлы зависимостей
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем исходный код
COPY . .

# Собираем приложение
RUN npm run build

# ---- Этап запуска ----
FROM nginx:alpine

# Копируем собранные файлы
COPY --from=build /app/build /usr/share/nginx/html

# Удаляем стандартный конфиг и копируем наш
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]