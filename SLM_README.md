# Project Documentation

## 1. Overview

This document describes the development workflow used in Asobo. It covers task management with a Kanban board, branching strategy, pull request process, and the concrete steps followed to implement a user story from assignment to merge.

**Public GitHub repository:**  
https://github.com/asobo-Vienna/asobo

**Kanban Board:**  
https://github.com/orgs/asobo-Vienna/projects/3

---

## 2. Tools and Setup

- **Version Control:** Git
- **Hosting Platform:** GitHub (public repository)
- **Project Management:** GitHub Projects (Kanban Board)
- **Branching Model:** Feature branches based on `master`
- **Code Review:** GitHub Pull Requests

---

## 3. Kanban Board Workflow

The project uses a Kanban board to track progress of user stories and tasks.


### Kanban Columns

- **Backlog:** All planned user stories that are not yet scheduled
- **Ready:** User stories ready to be implemented
- **In Progress:** Tasks currently being worked on
- **Review:** Tasks with an open pull request awaiting review
- **Done:** Completed and merged user stories

**Screenshot:**  
![alt text](image.png)
> Screenshot of the Kanban board showing all columns and at least one user story in each relevant state.
---

## 4. Workflow After a User Story Is Assigned

The following steps describe the actions taken after a user story is assigned to a developer.

### Step 1: Move User Story to In Progress

Once the user story is assigned, it is moved from **To Do** to **In Progress** on the Kanban board.

---

### Step 2: Create a Feature Branch

A new feature branch is created from the `master` branch.

Branch naming convention: `feature/<ticket-number>-<short-description>`  
Example: `feature/123-user-login`

**Screenshots:**  
![alt text](image-1.png)
![alt text](image-2.png)

> Screenshot showing the new branch in GitHub and the branch creation via command line.

---

### Step 3: Implement the User Story

The required code changes are implemented on the feature branch.

Commit rules:
- Small and focused
- Clearly described (e.g. `Add login form validation`)

During implementation, the Kanban card remains in **In Progress**.

---

## 5. Pull Request Process

### Step 4: Open a Pull Request

After completing the implementation, a pull request (PR) is opened from the feature branch into `master`.

The pull request includes:
- A short description of the implemented user story
- A reference to the related Kanban card or issue

The Kanban card is moved to **Review**.

**Screenshot:**  
![alt text](image-3.png)
> Screenshot of the open pull request (in this case still marked as draft) on GitHub.

---

### Step 5: Review and Fixes

If review comments are present, required changes are applied on the same feature branch and pushed to the remote branch.

---

## 6. Merge and Completion

### Step 6: Merge Pull Request

Once the pull request is approved and all checks pass:
- The pull request is merged into `master`
- The feature branch is deleted automatically
- The Kanban card is moved to **Done**

**Screenshot:**  
![alt text](image-4.png)
![alt text](image-5.png)
> Screenshot showing the merged pull request (as a commit on the `master` branch) and the updated Kanban cards.

---

## 7. Exemplary User Story

**User Story:**  
_As a registered user, I want to create an event so that other users can participate and view its media and comments._

### Process Summary

1. User story added to Backlog
2. Moved to Ready
3. Assigned and moved to In Progress
4. Feature branch `feature/124-backend-create-event` created
5. Event creation functionality implemented with several commits on feature branches (may involve multiple tickets)
6. Pull request(s) opened and reviewed
7. Pull request(s) merged into `master`
8. User story moved to Done

---

## 8. Conclusion

This workflow ensures proper tracking from user stories to code, supports parallel development through feature branches, and maintains code quality via pull requests and reviews.
