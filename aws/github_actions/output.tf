output "role_arn_to_assume" {
  description = "GitHub Actionsで指定するAssume対象ロールのARN"
  value       = aws_iam_role.github_actions.arn
}
