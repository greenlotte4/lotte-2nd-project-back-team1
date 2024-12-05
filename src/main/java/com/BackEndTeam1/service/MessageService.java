package com.BackEndTeam1.service;

import com.BackEndTeam1.dto.ChannelDTO;
import com.BackEndTeam1.dto.UserDTO;
import com.BackEndTeam1.entity.Channel;
import com.BackEndTeam1.entity.User;
import com.BackEndTeam1.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Service
public class MessageService {
    private final ChannelRepository channelRepository;
    private final ModelMapper modelMapper;

    public ChannelDTO saveChannel(ChannelDTO channelDTO) {
        Channel channel = modelMapper.map(channelDTO, Channel.class);
        User user = User.builder().userId(channelDTO.getManager()).build();
        channel.setManager(user);

        Channel savedChannel = channelRepository.save(channel);
        return modelMapper.map(savedChannel, ChannelDTO.class);
    }
}
