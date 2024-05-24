package io.geekya215.nyaoj.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileChecker {
    private FileChecker() {
    }

    static final byte[] ZIP_MAGIC = {'P', 'K', 0x03, 0x04};
    static final byte[] PDF_MAGIC = {'%', 'P', 'D', 'F', '-'};

    static final Pattern IN_FILE_PATTERN = Pattern.compile("\\d+\\.in");
    static final Pattern ANS_FILE_PATTERN = Pattern.compile("\\d+\\.ans");

    public static boolean isZip(final MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            final byte[] header = is.readNBytes(ZIP_MAGIC.length);
            return Arrays.compare(ZIP_MAGIC, header) == 0;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isPdf(final MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            final byte[] header = is.readNBytes(PDF_MAGIC.length);
            return Arrays.compare(PDF_MAGIC, header) == 0;
        } catch (IOException e) {
            return false;
        }
    }

    // sample and testcase zip file should follow this pattern
    // 1.in
    // 1.ans
    // 2.in
    // 2.ans
    // ...
    public static boolean validateZip(final MultipartFile file) {
        try (final InputStream is = file.getInputStream();
             final ZipInputStream zis = new ZipInputStream(is)) {

            final Set<String> inFiles = new HashSet<>();
            final Set<String> ansFiles = new HashSet<>();

            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                final String fileName = zipEntry.getName();
                if (IN_FILE_PATTERN.matcher(fileName).matches()) {
                    final String inSequence = fileName.replace(".in", "");
                    if (!inFiles.add(inSequence)) {
                        return false;
                    }
                } else if (ANS_FILE_PATTERN.matcher(fileName).matches()) {
                    final String ansSequence = fileName.replace(".ans", "");
                    if (!ansFiles.add(ansSequence)) {
                        return false;
                    }
                } else {
                    return false;
                }
                zis.closeEntry();
            }

            return inFiles.equals(ansFiles);

        } catch (IOException e) {
            return false;
        }

    }
}
