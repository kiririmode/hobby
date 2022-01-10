resource "aws_iam_user" "movie_uploader" {
  name = "MovieUploader"
}

resource "aws_iam_user_policy" "movie_uploader" {
  name = "MovieUploaderPolicy"
  user = aws_iam_user.movie_uploader.name
  policy = data.aws_iam_policy_document.uploader_policy.json
}

data "aws_iam_policy_document" "uploader_policy" {
  statement {
    actions   = [
      "s3:PutObject",
      "s3:PutObjectAcl",
    ]
    resources = [
      "${aws_s3_bucket.memories.arn}/*"
    ]
  }
}
