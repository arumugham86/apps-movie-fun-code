package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;

    public AlbumsController(AlbumsBean albumsBean, BlobStore bstore) {
        this.albumsBean = albumsBean;
        this.blobStore = bstore;
    }

    BlobStore blobStore;

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {

        Blob image = new Blob(getCoverFileName(albumId), uploadedFile.getInputStream(),
                                new Tika().detect(uploadedFile.getInputStream()));

        blobStore.put(image);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {

        Optional<Blob> image = blobStore.get(getCoverFileName(albumId));

        Blob blob = image.orElse(defaultImage(albumId));

        byte[] imageBytes = IOUtils.toByteArray(blob.inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(blob.contentType));
        headers.setContentLength(imageBytes.length);

        return new HttpEntity<>(imageBytes, headers);
    }

    private Blob defaultImage(long albumId) throws IOException {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("default-cover.jpg");

        return new Blob(getCoverFileName(albumId), resourceAsStream,
                new Tika().detect(getClass().getClassLoader().getResource("default-cover.jpg")));
    }

    private String getCoverFileName(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }
}
