package com.mitchtalmadge.cryptex.domain.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Arrays;
import java.util.Objects;

/**
 * Allows for storage of files in the database.
 */
@Entity(name = "file")
public class FileEntity {

    /**
     * The unique ID of the entity.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the file.
     */
    private String name;

    /**
     * The binary contents of the file.
     */
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] contents;

    /**
     * Constructs an empty file entity. Name must be specified before saving.
     */
    public FileEntity() {
    }

    /**
     * Constructs a file entity with the given name and contents.
     *
     * @param name     The name of the file.
     * @param contents The binary contents of the file.
     */
    public FileEntity(String name, byte[] contents) {
        this.name = name;
        this.contents = contents;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(contents);
        return result;
    }
}