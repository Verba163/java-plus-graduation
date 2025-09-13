package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategoryEntity(NewCategoryDto newCategoryDto);

}

