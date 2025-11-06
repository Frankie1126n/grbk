# Deployment Guide

## Environment Variables

### Frontend Environment Variables

Create `.env.production` file in the frontend directory:

```env
VUE_APP_API_BASE_URL=/api
VUE_APP_DEFAULT_AVATAR=/uploads/default-avatar.png
```

### Backend Environment Variables

Set the following environment variables for the backend:

```bash
# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/blog_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRATION=1800000

# File Upload Configuration
UPLOAD_PATH=/app/uploads/

# Mail Configuration (optional)
MAIL_HOST=your_smtp_host
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
```

## Build Process

### Frontend Build

```bash
cd frontend
npm install
npm run build
```

The built files will be in the `dist` directory.

### Backend Build

```bash
cd backend
mvn clean package
```

The built JAR file will be in the `target` directory.

## Deployment Steps

1. Set up the MySQL database and import the SQL files from the `database` directory
2. Configure environment variables as shown above
3. Build the frontend and backend as described
4. Deploy the built frontend files to your web server
5. Deploy the backend JAR file and run it with:
   ```bash
   java -jar backend-0.0.1-SNAPSHOT.jar
   ```

## File Upload Configuration

The application will automatically create the following directory structure under the upload path:
- `/avatars/` - User avatars
- `/covers/` - Blog covers
- `/content/` - Images used in blog content
- `/backgrounds/` - Background images

Make sure the application has write permissions to the upload directory.

## Reverse Proxy Configuration

If using Nginx, configure it to serve the frontend files and proxy API requests to the backend:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # Serve frontend files
    location / {
        root /path/to/frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # Proxy API requests to backend
    location /api/ {
        proxy_pass ;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # Serve uploaded files
    location /uploads/ {
        alias /app/uploads/;
    }
}
```