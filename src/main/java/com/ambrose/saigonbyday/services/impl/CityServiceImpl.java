package com.ambrose.saigonbyday.services.impl;


import com.ambrose.saigonbyday.config.CustomValidationException;
import com.ambrose.saigonbyday.config.ResponseUtil;
import com.ambrose.saigonbyday.converter.GenericConverter;
import com.ambrose.saigonbyday.dto.CityDTO;
import com.ambrose.saigonbyday.entities.City;
import com.ambrose.saigonbyday.entities.Destination;
import com.ambrose.saigonbyday.repository.CityRepository;
import com.ambrose.saigonbyday.repository.DestinationRepository;
import com.ambrose.saigonbyday.services.CityService;
import com.ambrose.saigonbyday.services.ServiceUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

  private final CityRepository cityRepository;
  private final DestinationRepository destinationRepository;
  private final GenericConverter genericConverter;




  private void loadDestinationsFromListDestinationIds(List<Long> requestDestinationIds, Long cityId){
    if (requestDestinationIds != null && !requestDestinationIds.isEmpty()){
      for (Long destinationId : requestDestinationIds){
        Destination destination = destinationRepository.findById(destinationId);
        destination.setCity(cityRepository.findById(cityId));
        destinationRepository.save(destination);
      }
    }
  }

  private CityDTO convertCityToCityDTO(City city){
    CityDTO newCityDTO = (CityDTO) genericConverter.toDTO(city, CityDTO.class);

    List<Destination> destinations = cityRepository.findDestinationByCityId(city.getId());

    if (destinations == null){
      newCityDTO.setDestinationIds(null);
    }
    else
    {
      List<Long> destinationIds = destinations.stream()
          .map(Destination::getId)
          .toList();
      newCityDTO.setDestinationIds(destinationIds);
    }
    return newCityDTO;
  }


  @Override
  public ResponseEntity<?> save(CityDTO cityDTO) {
    try{
      City city;
      List<Long> requestDestinationIds = cityDTO.getDestinationIds();

      if (requestDestinationIds != null){
        ServiceUtils.validateDestinationIds(requestDestinationIds, destinationRepository);
      }
      if (!ServiceUtils.errors.isEmpty()) {
        throw new CustomValidationException(ServiceUtils.errors);
      }

      if (cityDTO.getId() != null){

        City oldEntity = cityRepository.findById(cityDTO.getId());
        City tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);

        city = (City) genericConverter.toEntity(cityDTO, City.class);
        ServiceUtils.fillMissingAttribute(city, tempOldEntity);

        if (requestDestinationIds != null){
          destinationRepository.findAllByCityId(cityDTO.getId()).stream()
              .peek(destination -> destination.setCity(null))
              .forEach(destinationRepository::save);
        }
        loadDestinationsFromListDestinationIds(requestDestinationIds, city.getId());

        cityRepository.save(city);

      }
      else
      {
        cityDTO.setStatus(true);
        city = (City) genericConverter.toEntity(cityDTO, City.class);
        cityRepository.save(city);
        loadDestinationsFromListDestinationIds(requestDestinationIds, city.getId());
      }

      CityDTO result = convertCityToCityDTO(city);

      return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> changeStatus(Long id) {
    try{
      City city = cityRepository.findById(id);
      if(city != null){
        if (city.getStatus()){
          city.setStatus(false);
        } else {
          city.setStatus(true);
        }
        cityRepository.save(city);
        return ResponseUtil.getObject(null, HttpStatus.OK, "Status changed successfully");
      }else{
        return ResponseUtil.error("City not found", "Cannot change status of non-existing City", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }

  }

  @Override
  public Boolean checkExist(Long id) {
    City city = cityRepository.findById(id);
    return city != null;
  }

  @Override
  public ResponseEntity<?> findById(Long id) {
    try{
      City city = cityRepository.findByStatusIsTrueAndId(id);
      if (city != null){
        CityDTO result = convertCityToCityDTO(city);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
      }else{
        return ResponseUtil.error("City not found", "Cannot Find City", HttpStatus.NOT_FOUND);
      }
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
    try{
      Pageable pageable = PageRequest.of(page - 1, limit);
      List<City> cities = cityRepository.findAllByStatusIsTrue(pageable);
      List<CityDTO> result = new ArrayList<>();

      convertListCityToListCityDTO(cities, result);

      return ResponseUtil.getCollection(result,
          HttpStatus.OK,
          "Fetched successfully",
          page,
          limit,
          cityRepository.countAllByStatusIsTrue());
    }
    catch (Exception ex){
      return ResponseUtil.error(ex.getMessage(), "Failed", HttpStatus.BAD_REQUEST);
    }


  }

  @Override
  public ResponseEntity<?> findAll(int page, int limit) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<City> entities = cityRepository.findAllBy(pageable);
    List<CityDTO> result = new ArrayList<>();

    convertListCityToListCityDTO(entities, result);

    return ResponseUtil.getCollection(result,
        HttpStatus.OK,
        "Fetched successfully",
        page,
        limit,
        cityRepository.countAllByStatusIsTrue());
  }

  private void convertListCityToListCityDTO(List<City> cities, List<CityDTO> result){
    for (City city : cities){
      CityDTO newCityDTO = convertCityToCityDTO(city);
      result.add(newCityDTO);
    }
  }


  @Override
  public void saveCitiesFromExcel(String filePath) throws IOException{
    List<CityDTO> cityDTOList = readExcelFile(filePath);
    for (CityDTO cityDTO : cityDTOList) {
      City city = (City) genericConverter.toEntity(cityDTO, City.class);
      cityRepository.save(city);
    }
  }

  private List<CityDTO> readExcelFile(String filePath) throws IOException {
    List<CityDTO> cityDTOList = new ArrayList<>();
    try (InputStream inputStream = new FileInputStream(filePath)) {
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet sheet = workbook.getSheetAt(0);
      for (Row row : sheet) {
        if (row.getRowNum() == 0) {
          // Skip the header row
          continue;
        }

        CityDTO cityDTO = new CityDTO();
        cityDTO.setName(getCellValue(row.getCell(0)));
        cityDTO.setCode(getCellValue(row.getCell(1)));
        cityDTO.setStatus(true); // set default status

        cityDTOList.add(cityDTO);
      }
    }
    return cityDTOList;


  }
  private String getCellValue(Cell cell) {
    if (cell == null) {
      return null;
    }
    if (cell.getCellType() == CellType.STRING) {
      return cell.getStringCellValue();
    } else if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf(cell.getNumericCellValue());
    } else {
      return null;
    }
  }


}
