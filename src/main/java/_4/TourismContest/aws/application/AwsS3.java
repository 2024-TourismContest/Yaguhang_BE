package _4.TourismContest.aws.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AwsS3 {
    private String key;  // 파일명
    private String path;  // url
}