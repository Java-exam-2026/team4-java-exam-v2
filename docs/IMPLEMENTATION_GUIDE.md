# 残りの実装ファイル一覧

このドキュメントでは、まだ作成されていない重要なファイルのコードを提供します。

## バックエンド (Spring Boot)

### 1. JwtRequestFilter.java

**場所:** `backend/src/main/java/com/javaexam/security/JwtRequestFilter.java`

```java
package com.javaexam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT parsing error: " + e.getMessage());
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        chain.doFilter(request, response);
    }
}
```

---

### 2. SecurityConfig.java

**場所:** `backend/src/main/java/com/javaexam/security/SecurityConfig.java`

```java
package com.javaexam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

### 3. AuthController.java

**場所:** `backend/src/main/java/com/javaexam/controller/AuthController.java`

```java
package com.javaexam.controller;

import com.javaexam.dto.AuthRequest;
import com.javaexam.dto.AuthResponse;
import com.javaexam.entity.User;
import com.javaexam.repository.UserRepository;
import com.javaexam.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtUtil.generateToken(userDetails, user.getRole());
        
        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),
                user.getDisplayName(),
                user.getRole()
        ));
    }
    
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getUsername(),
                user.getDisplayName(),
                user.getRole()
        ));
    }
}
```

---

### 4. data.sql (初期データ)

**場所:** `backend/src/main/resources/data.sql`

```sql
-- 既存データをクリア (開発時のみ)
-- DELETE FROM user_progress;
-- DELETE FROM questions;
-- DELETE FROM chapters;
-- DELETE FROM users;

-- ユーザーデータ (パスワードはBCryptでハッシュ化済み - 元のパスワード: admin123, user123)
INSERT INTO users (id, username, password, display_name, role) VALUES
(gen_random_uuid(), 'admin', '$2a$10$xJWN.2gPVX0mU8R1FQKGYuV5Yq0ZwKYOQQ3qP1v1dXx7kQgKL.Zzy', '管理者', 'ROLE_ADMIN')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (id, username, password, display_name, role) VALUES
(gen_random_uuid(), 'testuser', '$2a$10$xJWN.2gPVX0mU8R1FQKGYuV5Yq0ZwKYOQQ3qP1v1dXx7kQgKL.Zzy', 'テストユーザー', 'ROLE_USER')
ON CONFLICT (username) DO NOTHING;

-- 章データ
INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
(gen_random_uuid(), '1.1', 'Javaの基礎', 1)
ON CONFLICT (chapter_code) DO NOTHING;

INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
(gen_random_uuid(), '2.1', '変数とデータ型', 2)
ON CONFLICT (chapter_code) DO NOTHING;

INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
(gen_random_uuid(), '3.1', '演算子', 3)
ON CONFLICT (chapter_code) DO NOTHING;

INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
(gen_random_uuid(), '3.2', '整数型', 4)
ON CONFLICT (chapter_code) DO NOTHING;

-- サンプル問題データ (章3.2用に20問以上作成)
-- 注: 実際の運用では、章ごとに最低20問必要
```

---

## フロントエンド (Vue.js)

### 5. package.json

**場所:** `frontend/package.json`

```json
{
  "name": "java-exam-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "pinia": "^2.1.7",
    "axios": "^1.6.2"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

---

### 6. vite.config.js

**場所:** `frontend/vite.config.js`

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: true
  }
})
```

---

### 7. main.js

**場所:** `frontend/src/main.js`

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
```

---

## 次のステップ

1. 上記のファイルを対応する場所に作成
2. 残りのControllerとServiceを実装 (ChapterController, ProgressController, AdminQuestionController など)
3. フロントエンドの各View、Store、Serviceを実装
4. サンプル問題データを `data.sql` に追加
5. Dev Containerで起動テスト
6. 動作確認

詳細な実装については、各ドキュメント (`Docs/`) を参照してください。
