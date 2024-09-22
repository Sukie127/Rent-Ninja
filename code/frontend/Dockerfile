# Use the official Node.js 20 image as the base image
FROM node:20 AS build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker's caching
COPY package*.json ./

# Install the dependencies
RUN npm install

# Copy the rest of the application files
COPY . ./

# Build the app
RUN npm run build

# Use an official Nginx image to serve the built files
FROM nginx

# Copy the build output to the Nginx html folder
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
