-- ==================== ВСТАВКА РОЛЕЙ ====================
INSERT INTO roles (title)
SELECT 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE title = 'ROLE_USER');

INSERT INTO roles (title)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE title = 'ROLE_ADMIN');

-- ==================== ВСТАВКА АДМИНА ====================
INSERT INTO users (full_name, email, telephone_number, password, enabled)
SELECT 'Admin', 'admin123@admin.com', '+7(999)123-45-67', '$2a$11$vQuPLtwJy2UzNEVhQPFPh.prNNy5vhqXpYxelshR/Jaz8Tn7iMDNq', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin123@admin.com');

-- Привязка роли администратора (role_id = 2)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, 2
FROM users u
WHERE u.email = 'admin123@admin.com'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role_id = 2
);

-- ==================== ВСТАВКА КАТЕГОРИЙ ====================
INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Кошки', 0, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Кошки');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Собаки', 1, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Собаки');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Птицы', 2, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Птицы');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Грызуны', 3, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Грызуны');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Рептилии', 4, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Рептилии');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Другие', 5, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Другие');

INSERT INTO categories (title, display_order, is_hidden)
SELECT 'Зоотовары', 6, false
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE title = 'Зоотовары');
-- ==================== ВСТАВКА ПОДКАТЕГОРИЙ ====================

-- Подкатегории для Кошек
INSERT INTO subcategories (title, category_id)
SELECT 'Британские', id FROM categories WHERE title = 'Кошки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Британские' AND category_id = (SELECT id FROM categories WHERE title = 'Кошки'));

INSERT INTO subcategories (title, category_id)
SELECT 'Шотландские', id FROM categories WHERE title = 'Кошки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Шотландские' AND category_id = (SELECT id FROM categories WHERE title = 'Кошки'));

INSERT INTO subcategories (title, category_id)
SELECT 'Мейн-кун', id FROM categories WHERE title = 'Кошки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Мейн-кун' AND category_id = (SELECT id FROM categories WHERE title = 'Кошки'));

-- Подкатегории для Собак
INSERT INTO subcategories (title, category_id)
SELECT 'Овчарки', id FROM categories WHERE title = 'Собаки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Овчарки' AND category_id = (SELECT id FROM categories WHERE title = 'Собаки'));

INSERT INTO subcategories (title, category_id)
SELECT 'Хаски', id FROM categories WHERE title = 'Собаки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Хаски' AND category_id = (SELECT id FROM categories WHERE title = 'Собаки'));

INSERT INTO subcategories (title, category_id)
SELECT 'Доберманы', id FROM categories WHERE title = 'Собаки'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Доберманы' AND category_id = (SELECT id FROM categories WHERE title = 'Собаки'));

-- Подкатегории для Птиц
INSERT INTO subcategories (title, category_id)
SELECT 'Попугаи', id FROM categories WHERE title = 'Птицы'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Попугаи' AND category_id = (SELECT id FROM categories WHERE title = 'Птицы'));

INSERT INTO subcategories (title, category_id)
SELECT 'Канарейки', id FROM categories WHERE title = 'Птицы'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Канарейки' AND category_id = (SELECT id FROM categories WHERE title = 'Птицы'));

INSERT INTO subcategories (title, category_id)
SELECT 'Голуби', id FROM categories WHERE title = 'Птицы'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Голуби' AND category_id = (SELECT id FROM categories WHERE title = 'Птицы'));

-- Подкатегории для Грызунов
INSERT INTO subcategories (title, category_id)
SELECT 'Хомяки', id FROM categories WHERE title = 'Грызуны'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Хомяки' AND category_id = (SELECT id FROM categories WHERE title = 'Грызуны'));

INSERT INTO subcategories (title, category_id)
SELECT 'Морские свинки', id FROM categories WHERE title = 'Грызуны'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Морские свинки' AND category_id = (SELECT id FROM categories WHERE title = 'Грызуны'));

INSERT INTO subcategories (title, category_id)
SELECT 'Крысы', id FROM categories WHERE title = 'Грызуны'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Крысы' AND category_id = (SELECT id FROM categories WHERE title = 'Грызуны'));

-- Подкатегории для Рептилий
INSERT INTO subcategories (title, category_id)
SELECT 'Черепахи', id FROM categories WHERE title = 'Рептилии'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Черепахи' AND category_id = (SELECT id FROM categories WHERE title = 'Рептилии'));

INSERT INTO subcategories (title, category_id)
SELECT 'Ящерицы', id FROM categories WHERE title = 'Рептилии'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Ящерицы' AND category_id = (SELECT id FROM categories WHERE title = 'Рептилии'));

INSERT INTO subcategories (title, category_id)
SELECT 'Змеи', id FROM categories WHERE title = 'Рептилии'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Змеи' AND category_id = (SELECT id FROM categories WHERE title = 'Рептилии'));

-- Подкатегории для Другие
INSERT INTO subcategories (title, category_id)
SELECT 'Рыбки', id FROM categories WHERE title = 'Другие'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Рыбки' AND category_id = (SELECT id FROM categories WHERE title = 'Другие'));

INSERT INTO subcategories (title, category_id)
SELECT 'Улитки', id FROM categories WHERE title = 'Другие'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Улитки' AND category_id = (SELECT id FROM categories WHERE title = 'Другие'));

INSERT INTO subcategories (title, category_id)
SELECT 'Лягушки', id FROM categories WHERE title = 'Другие'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Лягушки' AND category_id = (SELECT id FROM categories WHERE title = 'Другие'));

-- Подкатегории для Зоотоваров
INSERT INTO subcategories (title, category_id)
SELECT 'Корм для кошек', id FROM categories WHERE title = 'Зоотовары'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Корм для кошек' AND category_id = (SELECT id FROM categories WHERE title = 'Зоотовары'));

INSERT INTO subcategories (title, category_id)
SELECT 'Корм для собак', id FROM categories WHERE title = 'Зоотовары'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Корм для собак' AND category_id = (SELECT id FROM categories WHERE title = 'Зоотовары'));

INSERT INTO subcategories (title, category_id)
SELECT 'Наполнители', id FROM categories WHERE title = 'Зоотовары'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Наполнители' AND category_id = (SELECT id FROM categories WHERE title = 'Зоотовары'));

INSERT INTO subcategories (title, category_id)
SELECT 'Клетки и переноски', id FROM categories WHERE title = 'Зоотовары'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Клетки и переноски' AND category_id = (SELECT id FROM categories WHERE title = 'Зоотовары'));

INSERT INTO subcategories (title, category_id)
SELECT 'Игрушки', id FROM categories WHERE title = 'Зоотовары'
AND NOT EXISTS (SELECT 1 FROM subcategories WHERE title = 'Игрушки' AND category_id = (SELECT id FROM categories WHERE title = 'Зоотовары'));