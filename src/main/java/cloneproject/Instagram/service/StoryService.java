package cloneproject.Instagram.service;

import cloneproject.Instagram.dto.error.ErrorResponse;
import cloneproject.Instagram.dto.story.StoryContentRequest;
import cloneproject.Instagram.dto.story.StoryUploadRequest;
import cloneproject.Instagram.entity.member.Member;
import cloneproject.Instagram.entity.member.MemberStory;
import cloneproject.Instagram.entity.story.Story;
import cloneproject.Instagram.entity.story.StoryContent;
import cloneproject.Instagram.entity.story.StoryImage;
import cloneproject.Instagram.exception.InvalidInputException;
import cloneproject.Instagram.exception.MemberDoesNotExistException;
import cloneproject.Instagram.exception.StoryImagesAndContentsMismatchException;
import cloneproject.Instagram.repository.MemberRepository;
import cloneproject.Instagram.repository.story.MemberStoryRepository;
import cloneproject.Instagram.repository.story.StoryContentRepository;
import cloneproject.Instagram.repository.story.StoryImageRepository;
import cloneproject.Instagram.repository.story.StoryRepository;
import cloneproject.Instagram.util.S3Uploader;
import cloneproject.Instagram.vo.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cloneproject.Instagram.dto.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoryService {

    private final MemberStoryRepository memberStoryRepository;
    private final StoryRepository storyRepository;
    private final StoryImageRepository storyImageRepository;
    private final StoryContentRepository storyContentRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public Long upload(StoryUploadRequest request) {
        final List<MultipartFile> storyImages = request.getStoryImages();
        request.getStoryContents().forEach(req -> {
            if (req.getContent().isBlank())
                req.setContent("");
        });
        final Map<Long, List<StoryContentRequest>> storyContentMap = request.getStoryContents().stream()
                .collect(Collectors.groupingBy(StoryContentRequest::getId));

        validateStoryUploadRequest(storyImages, storyContentMap);
        final Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberDoesNotExistException::new);

        final Story story = storyRepository.save(new Story(member));
        for (int i = 0; i < storyImages.size(); i++) {
            final Image image = s3Uploader.uploadImage(storyImages.get(i), "story");
            final StoryImage storyImage = storyImageRepository.save(new StoryImage(image, story));
            final List<StoryContentRequest> storyContents = storyContentMap.get(i + 1);
            for (StoryContentRequest storyContent : storyContents)
                storyContentRepository.save(new StoryContent(storyContent.getContent(), storyContent.getY(), storyContent.getX(), storyImage));
        }

        final Optional<MemberStory> findMemberStory = memberStoryRepository.findByMember(member);
        if (findMemberStory.isEmpty())
            memberStoryRepository.save(new MemberStory(member, story));
        else
            findMemberStory.get().updateStory(story);

        return story.getId();
    }

    private void validateStoryUploadRequest(List<MultipartFile> storyImages, Map<Long, List<StoryContentRequest>> storyContentMap) {
        final List<ErrorResponse.FieldError> errors = new ArrayList<>();
        if (storyImages.isEmpty()) {
            errors.add(new ErrorResponse.FieldError("images", "empty", INVALID_STORY_IMAGE.getMessage()));
            throw new InvalidInputException(errors);
        } else if (storyImages.size() != storyContentMap.size())
            throw new StoryImagesAndContentsMismatchException();
    }
}
