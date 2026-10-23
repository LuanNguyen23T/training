# Practice 1

### 1. Phân loại từ khóa
- **Interface:** 
  - Calculating area
  - Calculating perimeter
- **Class:** 
  - Square
  - Rectangle

### 2. Sơ đồ liên kết
```text
        ┌──────────────────────────────────────────────────┐
        │                 interface Shape                  │
        │  • Calculating area                              │
        │  • Calculating perimeter                         │
        └────────────────────────┬─────────────────────────┘
                                 │
                                 │ implements
            ┌────────────────────┴────────────────────┐
            ▼                                         ▼
┌───────────────────────┐                 ┌───────────────────────┐
│    class Rectangle    │                 │      class Square     │
└───────────────────────┘                 └───────────────────────┘
```

### 3. Mối quan hệ
- `Square` và `Rectangle` là các **Class** thực thi (`implements`) **Interface** chứa 2 phương thức `Calculating area` và `Calculating perimeter`.
- **Interface** định nghĩa hành vi cần có (WHAT), còn **Class** định nghĩa cách thực hiện cụ thể (HOW).

---

# Practice 2

### Inheritance Diagram

```text
               ┌───────────────────────────────┐
               │    interface CheckCorrect     │
               └───────────────┬───────────────┘
                               │
                               │ implements
                               ▼
               ┌───────────────────────────────┐
               │    abstract class Question    │
               └───────────────┬───────────────┘
                               │
            ┌──────────────────┴──────────────────┐
            │ extends                             │ extends
            ▼                                     ▼
┌───────────────────────┐             ┌───────────────────────┐
│  class YesNoQuestion  │             │  class ChoiceQuestion │
└───────────────────────┘             └───────────────────────┘
```

