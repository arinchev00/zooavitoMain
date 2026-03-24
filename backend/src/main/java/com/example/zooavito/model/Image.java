package com.example.zooavito.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "original_file_name")
    private String originalFileName;

    private Long size;

    @Column(name = "content_type")
    private String contentType;

    @Lob
    private byte[] bytes;

    @Transient
    private String base64Image;

    @Column(name = "is_main", nullable = false)
    private boolean isMain = false;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id != null && id.equals(image.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
