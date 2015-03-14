package jp.hubfactory.moco.service;

import java.util.Date;
import java.util.List;

import jp.hubfactory.moco.entity.User;
import jp.hubfactory.moco.entity.UserGirlVoice;
import jp.hubfactory.moco.repository.UserGirlVoiceRepository;
import jp.hubfactory.moco.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGirlVoiceRepository userGirlVoiceRepository;

    public User findUserByUserId(Long userId) {
        return userRepository.findOne(userId);
    }

    public User createUser(User user) {
        Date nowDate = new Date();
        User findRecord = findUserByUserId(user.getUserId());
        if (findRecord == null) {
            user.setUpdDatetime(nowDate);
            user.setInsDatetime(nowDate);
            return userRepository.save(user);
        }
        return null;
    }

    public List<UserGirlVoice> findUserGirlVoice(Long userId, Integer girlId) {
        return userGirlVoiceRepository.findByUserGirlVoiceKeyUserIdAndUserGirlVoiceKeyGirlId(userId, girlId);
    }
}
