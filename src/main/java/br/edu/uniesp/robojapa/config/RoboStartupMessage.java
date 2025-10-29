package br.edu.uniesp.robojapa.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RoboStartupMessage implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Garante que o diretório de destino existe
        String outputDir = "C:\\robocode\\robots\\iesp";
        if (!Files.exists(Paths.get(outputDir))) {
            Files.createDirectories(Paths.get(outputDir));
            System.out.println("📂 Diretório criado: " + outputDir);
        }

        // Carrega o banner do arquivo
        try (InputStream inputStream = new ClassPathResource("banner.txt").getInputStream()) {
            String banner = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("\n" + banner + "\n");
        } catch (Exception e) {
            System.out.println("✅ Aplicação iniciada com sucesso!");
        }
    }
}
