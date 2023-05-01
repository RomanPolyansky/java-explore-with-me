package ru.practicum.event.event.model.mapping;

import org.modelmapper.ModelMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.event.model.*;

public class EventMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(new EventNewDtoConverter());
        modelMapper.addConverter(new EventUpdateDtoConverter());
    }

    public static Event convertToEntity(EventRequestDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        if (eventDto.getCategory() != null) event.setCategory(new Category(eventDto.getCategory(), null));
        return event;
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

    public static EventResponseShortDto convertToShortDto(Event event) {
        return modelMapper.map(event, EventResponseShortDto.class);
    }
}
