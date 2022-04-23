variable "web_bucket_name" {
  type        = string
  description = "静的ウェブサイト用バケット名"
}

variable "logs_bucket_name" {
  type        = string
  description = "ログ用バケット名"
}

variable "s3_access_log_prefix" {
  type        = string
  description = "S3アクセスログの出力先となるPrefix"
}
