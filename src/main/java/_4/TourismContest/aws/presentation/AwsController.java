package _4.TourismContest.aws.presentation;

import _4.TourismContest.aws.application.AwsUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/aws")
public class AwsController {
    private final AwsUtils awsUtils;

    @PostMapping()
    public ResponseEntity<String> create(@RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        // 이미지 저장 후 url 리턴 받는 컨트롤러
        return new ResponseEntity<>(awsUtils.upload(multipartFile, "dir").getPath(), HttpStatus.OK);
    }

    // awsUtils.upload(multipartFile, "dir").getPath() 쓰면 저장하고 이미지 url 리턴 받음. "dir"에 원하는 폴더명 쓰면 됨.
}