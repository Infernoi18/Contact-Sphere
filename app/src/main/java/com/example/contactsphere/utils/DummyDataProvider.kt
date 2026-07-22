package com.example.contactsphere.utils

import com.example.contactsphere.model.Contact

object DummyDataProvider {

    fun getDummyContacts(): List<Contact> {
        return listOf(
            Contact(
                id = "1",
                name = "Alex Morgan",
                phone = "+1 (555) 234-5678",
                role = "Android Engineer",
                bio = "Mobile developer passionate about Kotlin, Clean Architecture, and building accessible, beautiful user interfaces. Enjoys hiking, photography, and open-source contributions during weekends.",
                isFavorite = true
            ),
            Contact(
                id = "2",
                name = "Sophia Chen",
                phone = "+1 (555) 876-5432",
                role = "UI/UX Designer",
                bio = "Crafting user-centered visual experiences for over 6 years. Specializes in design systems, accessibility compliance, and interactive prototyping using Figma.",
                isFavorite = false
            ),
            Contact(
                id = "3",
                name = "Marcus Vance",
                phone = "+1 (555) 345-6789",
                role = "Backend Specialist",
                bio = "Architecting scalable distributed systems, RESTful APIs, and microservices in Go and Java. Passionate about system performance, database indexing, and automated DevOps pipelines.",
                isFavorite = true
            ),
            Contact(
                id = "4",
                name = "Elena Rostova",
                phone = "+1 (555) 987-6543",
                role = "Product Manager",
                bio = "Data-driven product strategist transforming complex user problems into intuitive digital products. Leads cross-functional teams using agile methodologies to ship impactful features.",
                isFavorite = false
            ),
            Contact(
                id = "5",
                name = "David Kalu",
                phone = "+1 (555) 456-7890",
                role = "DevOps Lead",
                bio = "Infrastructure automation enthusiast managing cloud native architectures on AWS and Kubernetes. Focused on CI/CD pipeline reliability, monitoring, and security best practices.",
                isFavorite = true
            ),
            Contact(
                id = "6",
                name = "Jessica Taylor",
                phone = "+1 (555) 654-3210",
                role = "QA Automation Lead",
                bio = "Ensuring high software quality standards through robust automated testing frameworks using Espresso, Selenium, and Appium.",
                isFavorite = false
            ),
            Contact(
                id = "7",
                name = "Liam O'Connor",
                phone = "+1 (555) 789-0123",
                role = "Security Analyst",
                bio = "Penetration testing and application security consultant dedicated to discovering vulnerabilities and implementing strict security compliance measures across enterprise mobile apps.",
                isFavorite = false
            ),
            Contact(
                id = "8",
                name = "Priya Patel",
                phone = "+1 (555) 321-0987",
                role = "Data Scientist",
                bio = "Machine learning enthusiast building predictive models and natural language processing pipelines using Python, PyTorch, and cloud compute services.",
                isFavorite = true
            )
        )
    }
}
