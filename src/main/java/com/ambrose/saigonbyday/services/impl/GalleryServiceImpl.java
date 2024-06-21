package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.GalleryDTO;
import com.ambrose.saigonbyday.entities.Gallery;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.repository.GalleryRepository;
import com.ambrose.saigonbyday.services.GalleryService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {
  private final GalleryRepository galleryRepository;
  private final DestinationRepository destinationRepository;
  private final GenericConverter genericConverter;


  @Override
  public ResponseEntity<?> save(GalleryDTO galleryDTO) {
    try{
      Gallery gallery;

      if (galleryDTO.getId() != null){
        Long id = galleryDTO.getId();
        Gallery oldEntity = galleryRepository.findById(id);
        Gallery tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);

        gallery = (Gallery) genericConverter.toEntity(galleryDTO, Gallery.class);
        ServiceUtils.fillMissingAttribute(gallery, tempOldEntity);
        galleryRepository.save(gallery);

      }
      else{
        galleryDTO.setStatus(true);
        gallery = (Gallery) genericConverter.toEntity(galleryDTO, Gallery.class);
        galleryRepository.save(gallery);
      }
      GalleryDTO result = (GalleryDTO) genericConverter.toDTO(gallery, GalleryDTO.class);

      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Save Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    Gallery gallery = galleryRepository.findByStatusIsTrueAndId(id);
    if (gallery != null){
      GalleryDTO result = (GalleryDTO) genericConverter.toDTO(gallery, GalleryDTO.class);
      return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }else{
      return ResponseUtil.error("Gallery not found", "Cannot Find Gallery", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    Gallery gallery = galleryRepository.findById(id);
    if (gallery != null){
      if (gallery.getStatus()){
        gallery.setStatus(false);
      }else{
        gallery.setStatus(true);
      }
      galleryRepository.save(gallery);
      return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
    }else{
      return ResponseUtil.error("Gallery not found", "Cannot change status of non-existing Gallery", HttpStatus.NOT_FOUND);
    }
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Gallery> galleryList = galleryRepository.findAllByStatusIsTrue(pageable);
    List<GalleryDTO> result = new ArrayList<>();
    convertListGalleryToListGalleryDTO(galleryList, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        galleryRepository.countAllByStatusIsTrue());

  }

  @Override
  public Boolean checkExist(Long id) {
    Gallery gallery = galleryRepository.findById(id);
    return gallery != null;
  }

  private void convertListGalleryToListGalleryDTO(List<Gallery> galleries, List<GalleryDTO> result){
    for (Gallery gallery : galleries){
      GalleryDTO newGalleryDTO = (GalleryDTO) genericConverter.toDTO(gallery, GalleryDTO.class);
      result.add(newGalleryDTO);
    }
  }

}
