# Team Setup Guide - ZYNK HRMS

## 🔐 Authentication Required for Push

You need to authenticate with GitHub to push code. Choose one of these methods:

### Option 1: Personal Access Token (Recommended)

1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate a new token with `repo` permissions
3. Copy the token
4. When pushing, use the token as password:
   ```bash
   git push -u origin main
   # Username: your-github-username
   # Password: paste-your-token-here
   ```

### Option 2: SSH Key (More Secure)

1. Generate SSH key:
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```

2. Add to SSH agent:
   ```bash
   eval "$(ssh-agent -s)"
   ssh-add ~/.ssh/id_ed25519
   ```

3. Copy public key:
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```

4. Add to GitHub: Settings → SSH and GPG keys → New SSH key

5. Change remote to SSH:
   ```bash
   git remote set-url origin git@github.com:ROHIT-JOSHI/zynk_backend.git
   git push -u origin main
   ```

### Option 3: GitHub CLI

```bash
# Install GitHub CLI, then:
gh auth login
git push -u origin main
```

---

## 👥 For Team Members - Getting Started

### Step 1: Clone the Repository

```bash
git clone https://github.com/ROHIT-JOSHI/zynk_backend.git
cd zynk_backend
```

### Step 2: Verify Setup

```bash
# Check if Java is installed
java -version

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### Step 3: Create Your First Branch

**IMPORTANT:** Always work on a separate branch, never directly on `main`!

```bash
# Create and switch to a new branch
git checkout -b feature/your-name-initial-setup

# Example: git checkout -b feature/tsharma-initial-setup
```

### Step 4: Make Changes and Commit

```bash
# Make your changes to files
# Then stage and commit:
git add .
git commit -m "Your descriptive commit message"
```

### Step 5: Push Your Branch

```bash
git push origin feature/your-name-initial-setup
```

### Step 6: Create Pull Request

1. Go to GitHub repository: https://github.com/ROHIT-JOSHI/zynk_backend
2. Click "Pull requests" → "New pull request"
3. Select your branch
4. Add description and create PR
5. Wait for review and approval

---

## 🔄 Daily Workflow

### Starting Work (Every Day)

```bash
# 1. Update your local main branch
git checkout main
git pull origin main

# 2. Create a new branch for your work
git checkout -b feature/your-feature-name

# 3. Start coding!
```

### During Development

```bash
# Save your work frequently
git add .
git commit -m "WIP: Description of what you're working on"

# Push to your branch
git push origin feature/your-feature-name
```

### Finishing Your Work

```bash
# Final commit
git add .
git commit -m "Complete: Feature description"

# Push and create PR
git push origin feature/your-feature-name
```

---

## 📋 Branch Naming Convention

Use these prefixes for branches:

- `feature/` - New features (e.g., `feature/user-authentication`)
- `bugfix/` - Bug fixes (e.g., `bugfix/login-error`)
- `hotfix/` - Urgent production fixes (e.g., `hotfix/security-patch`)
- `refactor/` - Code refactoring (e.g., `refactor/database-layer`)

**Format:** `type/your-name-short-description`

**Examples:**
- `feature/tsharma-employee-crud`
- `bugfix/rjoshi-api-validation`
- `feature/amember-dashboard-ui`

---

## ✅ Best Practices

1. **Always pull latest changes before starting:**
   ```bash
   git checkout main
   git pull origin main
   ```

2. **Never commit directly to main branch**

3. **Write clear commit messages:**
   - Good: `"Add user authentication with JWT tokens"`
   - Bad: `"fix"` or `"update"`

4. **Keep commits small and focused** - One feature/fix per commit

5. **Test before pushing** - Make sure your code compiles and runs

6. **Resolve conflicts early** - If you see conflicts, resolve them before pushing

---

## 🚨 Handling Merge Conflicts

If you encounter conflicts:

```bash
# 1. Pull latest main
git checkout main
git pull origin main

# 2. Merge main into your branch
git checkout feature/your-branch
git merge main

# 3. Resolve conflicts in your editor
# 4. Stage resolved files
git add .

# 5. Complete merge
git commit -m "Merge main into feature/your-branch"

# 6. Push
git push origin feature/your-branch
```

---

## 📞 Need Help?

- Check the main README.md for project details
- Create an issue on GitHub for bugs/questions
- Ask team members in your communication channel

---

**Happy Collaborating! 🎉**

