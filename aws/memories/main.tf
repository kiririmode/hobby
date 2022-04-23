provider "aws" {
  region = "ap-northeast-1"
  default_tags {
    tags = {
      ManagedBy = "Terraformer"
    }
  }
}

resource "aws_s3_bucket" "memories" {
  bucket = var.web_bucket_name
  acl    = "private"

  force_destroy = true

  website {
    index_document = "index.html"
  }
  logging {
    target_bucket = aws_s3_bucket.logs.id
    target_prefix = var.s3_access_log_prefix
  }
  versioning {
    enabled = false
  }

  tags = {
    Name = "Home Memories"
  }
}

# TODO: CloudFront経由にした時は不要になるはず
resource "aws_s3_bucket_policy" "p" {
  bucket = aws_s3_bucket.memories.id
  policy = data.aws_iam_policy_document.public_read.json
}

data "aws_iam_policy_document" "public_read" {
  statement {
    sid    = "PublicReadGetObject"
    effect = "Allow"
    principals {
      type        = "*"
      identifiers = ["*"]
    }
    actions = [
      "s3:GetObject"
    ]
    resources = ["arn:aws:s3:::${var.web_bucket_name}/*"]
  }
}

resource "aws_s3_bucket_object" "index" {
  bucket = aws_s3_bucket.memories.id

  key          = "index.html"
  source       = "./docs/index.html"
  etag         = filemd5("./docs/index.html")
  content_type = "text/html"

  storage_class = "REDUCED_REDUNDANCY"
}
