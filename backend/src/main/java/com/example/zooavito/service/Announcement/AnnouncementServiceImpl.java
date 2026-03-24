package com.example.zooavito.service.Announcement;

import com.example.zooavito.model.*;
import com.example.zooavito.repository.*;
import com.example.zooavito.request.AnnouncementRequest;
import com.example.zooavito.response.AnnouncementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAllAnnouncements(Long categoryId, Long subcategoryId, Integer minPrice, Integer maxPrice) {
        log.info("=== ПОЛУЧЕНИЕ ВСЕХ ОБЪЯВЛЕНИЙ С ФИЛЬТРАЦИЕЙ ===");

        Specification<Announcement> spec = Specification.where(null);

        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("categories").get("id"), categoryId));
        }

        if (subcategoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("subcategories").get("id"), subcategoryId));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        List<Announcement> announcements = announcementRepository.findAll(spec);
        log.info("Найдено объявлений: {}", announcements.size());

        return announcements.stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AnnouncementResponse> getAnnouncementsByUserEmail(String userEmail) {
        log.info("=== ПОЛУЧЕНИЕ ОБЪЯВЛЕНИЙ ПОЛЬЗОВАТЕЛЯ === email: {}", userEmail);

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        List<Announcement> announcements = announcementRepository.findByUserId(user.getId());

        log.info("Найдено объявлений: {}", announcements.size());

        return announcements.stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request, @Nullable List<MultipartFile> files, String userEmail) throws IOException {
        log.info("=== СОЗДАНИЕ ОБЪЯВЛЕНИЯ ===");
        log.info("Заголовок: {}, файлов: {}", request.getTitle(), files != null ? files.size() : 0);

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден");
        }

        Subcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Подкатегория не найдена: " + request.getSubcategoryId()
                ));

        Category category = subcategory.getCategory();

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setPrice(request.getPrice());
        announcement.setDescription(request.getDescription());
        announcement.setDateOfPublication(LocalDate.now());
        announcement.setUser(user);

        Set<Category> categories = new HashSet<>();
        categories.add(category);
        announcement.setCategories(categories);

        Set<Subcategory> subcategories = new HashSet<>();
        subcategories.add(subcategory);
        announcement.setSubcategories(subcategories);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        log.info("Объявление сохранено с ID: {}", savedAnnouncement.getId());

        if (files != null && !files.isEmpty()) {
            log.info("Обработка {} изображений", files.size());

            Set<Image> savedImages = new HashSet<>();

            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    log.info("Конвертация файла: {}", file.getOriginalFilename());

                    Image image = convertToImage(file);
                    image.setAnnouncement(savedAnnouncement);

                    Image savedImage = imageRepository.save(image);
                    savedImages.add(savedImage);

                    log.info("✅ Изображение сохранено: {}, ID: {}, размер: {} байт",
                            file.getOriginalFilename(), savedImage.getId(), file.getSize());
                }
            }

            if (!savedImages.isEmpty()) {
                savedAnnouncement.setImages(savedImages);
                log.info("Всего изображений добавлено: {}", savedImages.size());
            }
        } else {
            log.info("Объявление создано без изображений");
        }

        Announcement fullAnnouncement = announcementRepository.findById(savedAnnouncement.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

        return AnnouncementResponse.from(fullAnnouncement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .map(AnnouncementResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление не найдено с id: " + id
                ));
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request,
                                                   @Nullable List<MultipartFile> newFiles,
                                                   @Nullable List<Long> imagesToDelete,
                                                   @Nullable Long mainImageId,  // добавить!
                                                   String userEmail) throws IOException {
        log.info("=== ОБНОВЛЕНИЕ ОБЪЯВЛЕНИЯ ID: {} ===", id);
        log.info("Новых файлов: {}, изображений для удаления: {}, главное фото ID: {}",
                newFiles != null ? newFiles.size() : 0,
                imagesToDelete != null ? imagesToDelete.size() : 0,
                mainImageId);

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление не найдено с id: " + id
                ));

        User currentUser = userRepository.findByEmail(userEmail);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден");
        }

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getTitle()));

        if (!announcement.getUser().getEmail().equals(userEmail) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на редактирование");
        }

        announcement.setTitle(request.getTitle());
        announcement.setPrice(request.getPrice());
        announcement.setDescription(request.getDescription());

        Subcategory newSubcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Подкатегория не найдена: " + request.getSubcategoryId()
                ));

        Set<Category> categories = new HashSet<>();
        categories.add(newSubcategory.getCategory());
        announcement.setCategories(categories);

        Set<Subcategory> subcategories = new HashSet<>();
        subcategories.add(newSubcategory);
        announcement.setSubcategories(subcategories);

        if (mainImageId != null) {
            announcement.getImages().forEach(img -> img.setMain(false));
        }

        if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
            log.info("Удаление {} изображений", imagesToDelete.size());

            for (Long imageId : imagesToDelete) {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Изображение не найдено: " + imageId
                        ));

                announcement.getImages().remove(image);
                imageRepository.delete(image);
            }
        }

        if (newFiles != null && !newFiles.isEmpty()) {
            log.info("Добавление {} новых изображений", newFiles.size());

            for (MultipartFile file : newFiles) {
                if (file != null && !file.isEmpty()) {
                    Image image = convertToImage(file);
                    image.setAnnouncement(announcement);
                    image.setMain(false);  // новые фото не главные по умолчанию
                    imageRepository.save(image);
                    announcement.getImages().add(image);
                    log.info("Добавлено новое изображение: {}", file.getOriginalFilename());
                }
            }
        }

        if (mainImageId != null) {
            Image mainImage = imageRepository.findById(mainImageId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Изображение не найдено: " + mainImageId
                    ));

            if (!mainImage.getAnnouncement().getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Изображение не принадлежит этому объявлению"
                );
            }

            mainImage.setMain(true);
            imageRepository.save(mainImage);
            log.info("Установлено главное изображение с ID: {}", mainImageId);
        }

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        log.info("Объявление обновлено, всего изображений: {}", updatedAnnouncement.getImages().size());

        return AnnouncementResponse.from(updatedAnnouncement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id, String userEmail) {
        log.info("=== УДАЛЕНИЕ ОБЪЯВЛЕНИЯ ID: {} ===", id);

        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Объявление не найдено с id: " + id
                ));

        User currentUser = userRepository.findByEmail(userEmail);
        if (currentUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Пользователь не найден"
            );
        }

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getTitle()));

        if (!announcement.getUser().getEmail().equals(userEmail) && !isAdmin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "У вас нет прав на удаление этого объявления"
            );
        }

        announcementRepository.delete(announcement);
        log.info("Объявление удалено");
    }

    private Image convertToImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());

        log.info("Изображение сконвертировано: {}, размер: {} байт, тип: {}",
                image.getOriginalFileName(), image.getSize(), image.getContentType());
        return image;
    }
}