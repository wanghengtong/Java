    @Override
    public User update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        User oldUser = USER_MAP.get(user.getId());
        if (oldUser == null) {
            throw new IllegalArgumentException("Old user not found");
        }
        
        oldUser.setName(user.getName());
        // 1.鏇存柊鏁版嵁搴
        USER_MAP.put(oldUser.getId(), oldUser);

        // 2.鏇存柊Caffeine缂撳瓨
        caffeineCache.put(oldUser.getId(), oldUser);

        // 3.鏇存柊Redis鏁版嵁搴
        try {
            redisTemplate.opsForValue().set(oldUser.getId(), objectMapper.writeValueAsString(oldUser), 20, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);