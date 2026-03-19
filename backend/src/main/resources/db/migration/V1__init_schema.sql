-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE NOT NULL
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telephone_number VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE NOT NULL
);

-- Связь пользователей и ролей (многие ко многим)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Таблица категорий
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) UNIQUE NOT NULL,
    display_order INTEGER DEFAULT 0,
    is_hidden BOOLEAN DEFAULT FALSE
);

-- Таблица подкатегорий
CREATE TABLE IF NOT EXISTS subcategories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    CONSTRAINT fk_subcategories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT uk_subcategory_title_per_category UNIQUE (title, category_id)
);

-- Таблица объявлений
CREATE TABLE IF NOT EXISTS announcement (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    description TEXT,
    comment TEXT,
    date_of_publication DATE NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_announcement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Связь объявлений и категорий (многие ко многим)
CREATE TABLE IF NOT EXISTS announcement_categories (
    announcement_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (announcement_id, category_id),
    CONSTRAINT fk_ann_categories_announcement FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    CONSTRAINT fk_ann_categories_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Связь объявлений и подкатегорий (многие ко многим)
CREATE TABLE IF NOT EXISTS announcement_subcategories (
    announcement_id BIGINT NOT NULL,
    subcategory_id BIGINT NOT NULL,
    PRIMARY KEY (announcement_id, subcategory_id),
    CONSTRAINT fk_ann_subcategories_announcement FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    CONSTRAINT fk_ann_subcategories_subcategory FOREIGN KEY (subcategory_id) REFERENCES subcategories(id) ON DELETE CASCADE
);

-- Таблица изображений
CREATE TABLE IF NOT EXISTS image (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    original_file_name VARCHAR(255),
    size BIGINT,
    content_type VARCHAR(255),
    bytes OID,
    announcement_id BIGINT,
    is_main BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_image_announcement FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE
);

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    announcement_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_comments_announcement FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_announcement_user_id ON announcement(user_id);
CREATE INDEX idx_announcement_date ON announcement(date_of_publication);
CREATE INDEX idx_comments_announcement_id ON comments(announcement_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);
CREATE INDEX idx_image_announcement_id ON image(announcement_id);
CREATE INDEX idx_subcategories_category_id ON subcategories(category_id);