output "uploader_name" {
  description = "動画アップロードユーザ名"
  value       = aws_iam_user.movie_uploader.name
}
