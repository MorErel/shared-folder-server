package com.project.sharedfolderserver.v1.file;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.io.FileUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.io.FileUtils;


@Data
@Entity
@Table
@Accessors(chain = true)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true)
    private String name;
    @Setter(AccessLevel.PACKAGE)
    @UpdateTimestamp
    @Column
    private Instant dateModified;
    @Setter(AccessLevel.PACKAGE)
    @CreationTimestamp
    @Column
    private Instant dateAdded;
    @Column
    private String kind;
    @Column
    private byte[] content;
    @Column
    private String size;

    public void setKind() {
        this.kind = extractKind();
    }

    public void setSize() {
        if (content == null) {
            this.size = "0";
        } else {
            this.size = FileUtils.byteCountToDisplaySize(content.length);
        }
    }

    private String extractKind() {
        return this.name.split("\\.")[1];
    }
}
