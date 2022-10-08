package com.project.sharedfolderserver.v1.file;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import javax.validation.constraints.Pattern;

@Data
@Entity
@Table
@Accessors(chain = true)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique=true)
    @Pattern(regexp="^[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9_\\-]+$", message="file name must be in the form of NAME.KIND using number, letters, dashes and underscores")
    private String name;
    @Setter(AccessLevel.PACKAGE)
    @UpdateTimestamp
    @Column
    private LocalDateTime dateModified;
    @Setter(AccessLevel.PACKAGE)
    @CreationTimestamp
    @Column
    private LocalDateTime dateAdded;
    @Column
    private String kind;
    @Column
    private byte[] content;
    @Column
    private String size;
    // TODO flyway

    public void setKind() {
        this.kind = extractKind();
    }

    public void setSize() {
        if (content == null)
            this.size = "0";
        else
            this.size = FileUtils.byteCountToDisplaySize(content.length);
    }

    private String extractKind() {
        return this.name.split("\\.")[1];
    }
}
