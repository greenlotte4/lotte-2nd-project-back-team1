package com.BackEndTeam1.config;

import com.BackEndTeam1.dto.TaskDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.ChatType;
import com.BackEndTeam1.entity.Task;
import com.BackEndTeam1.entity.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // 수동 매핑 규칙 추가
        modelMapper.typeMap(UserDTO.class, User.class).addMappings(mapper ->
                mapper.map(UserDTO::getPass, User::setPass)
        );

        // 수동 매핑 추가 - 강중원 2024.12.09
        // String -> Enum 변환 설정 (대소문자 무시하고 변환)
        modelMapper.addConverter(new Converter<String, ChatType>() {
            public ChatType convert(MappingContext<String, ChatType> context) {
                return ChatType.valueOf(context.getSource().toUpperCase());
            }
        });

        modelMapper.typeMap(User.class, String.class).setConverter(context -> {
            User user = context.getSource();
            return user != null ? user.getUserId() : null;
        });

        modelMapper.typeMap(Task.class, TaskDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getAssignee().getUserId(), TaskDTO::setAssignee);
        });

        return modelMapper;
    }

}