//package com.project.sharedfolderserver.v1.file;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class FileConfig {
//    @Bean
//    CommandLineRunner commandLineRunner(FileRepository fileRepository) {
//        return args -> {
//            File file1 = new File()
//                    .setName("FileA")
//                    .setKind("ZIP")
//                    .setSize("65 KB");
//            File file2 = new File()
//                    .setName("FileB")
//                    .setKind("PDF")
//                    .setSize("12 MB");
//            fileRepository.saveAll(List.of(file1,file2));
//        };
//    }
//}
