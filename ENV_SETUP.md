# Environment Configuration for Flutter Social Chat

## Overview

This application uses environment variables to manage sensitive API keys and secrets. This approach enhances security by keeping these values out of the source code repository.

## Required Environment Variables

The application requires the following environment variables:

- `STREAM_CHAT_API_KEY`: Your Stream Chat API key
- `STREAM_CHAT_API_SECRET`: Your Stream Chat API secret

## Setup Instructions

1. The repository includes a `.env.example` file with placeholder values
2. Create a `.env` file in the root of your project (same level as `pubspec.yaml`)
3. Copy the content from `.env.example` to your new `.env` file
4. Replace the placeholder values with your actual Stream Chat credentials

Example `.env` file content:
```
STREAM_CHAT_API_KEY=your_actual_stream_chat_api_key
STREAM_CHAT_API_SECRET=your_actual_stream_chat_api_secret
```

## Important Notes

- The `.env.example` file is included in the repository as a template
- Your actual `.env` file should NEVER be committed to version control
- The `.gitignore` file is configured to exclude `.env` but include `.env.example`
- Both files must be listed in the `assets` section in `pubspec.yaml` (already done)
- After updating environment files, run `flutter clean` and `flutter pub get` 

## Getting Stream Chat Credentials

1. Sign up or log in to [Stream](https://getstream.io/)
2. Create a new app or use an existing one
3. Navigate to the app dashboard
4. Find your API Key and API Secret in the app settings
5. Copy these values to your `.env` file 