package ru.practicum.event.model.mapping;

import org.modelmapper.ModelMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.*;

public class EventMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(new EventNewDtoConverter());
        modelMapper.addConverter(new EventUpdateDtoConverter());
    }

    public static Event convertToEntity(EventRequestDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        event.setCategory(new Category(eventDto.getCategory(), null));
        return event;
    }

    public static EventRequestDto convertToDto(Event event) {
        return modelMapper.map(event, EventRequestDto.class);
    }

    public static EventResponseNewDto convertToNewDto(Event event) {
        return modelMapper.map(event, EventResponseNewDto.class);
    }

    public static EventResponseFullDto convertToFullDto(Event event) {
        return modelMapper.map(event, EventResponseFullDto.class);
    }

    public static EventResponseUpdateDto convertToUpdateDto(Event event) {
        return modelMapper.map(event, EventResponseUpdateDto.class);
    }
}
